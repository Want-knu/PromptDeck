# PromptDeck GCP 배포 가이드

GCP Compute Engine VM 1대에 docker-compose로 PromptDeck 전체 스택(MySQL + Spring Boot + nginx)을 배포하는 방법.

## 1. 아키텍처

```
브라우저 → GCP 방화벽(80) → VM ┌─ nginx(80, 정적 React + /api 프록시)
                              ├─ Spring Boot(8080)
                              └─ MySQL 8(3306, 내부망 전용)
```

세 컨테이너는 Docker bridge 네트워크 `promptdeck-network`로 묶여 있으며, 외부에는 nginx의 80번 포트만 노출된다.

## 2. VM 사양

| 항목 | 값 | 비고 |
| --- | --- | --- |
| 리전 | `asia-northeast3` (Seoul) | 한국 사용자 대상 지연 최소화 |
| 머신 유형 | `e2-small` (2 vCPU, 2GB RAM) | Spring Boot Java 25 + MySQL 8 동시 가동 가능 최소 사양 |
| 부팅 디스크 | Ubuntu 22.04 LTS, 표준 영구 디스크 20GB | SSD 대비 비용 약 1/4 |
| 방화벽 | HTTP(80), HTTPS(443), SSH(22) 허용 | 8080/3306은 외부 차단 |
| IP | Ephemeral 외부 IP | 정지/시작 시 IP 변경됨 |

## 3. 사전 준비

VM에 다음 도구 설치:

```bash
sudo apt update && sudo apt upgrade -y
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
# 재로그인 후 docker run hello-world 로 동작 확인
```

## 4. 배포 절차

```bash
git clone https://github.com/Want-knu/PromptDeck.git
cd PromptDeck
git checkout develope
```

`.env` 파일 생성. 시크릿은 모두 새로 생성해야 한다:

```bash
echo "MYSQL_ROOT_PW : $(openssl rand -base64 24)"
echo "MYSQL_USER_PW : $(openssl rand -base64 24)"
echo "JWT_SECRET    : $(openssl rand -base64 48)"
echo "PROVIDER_PW   : $(openssl rand -base64 24)"
echo "PROVIDER_SALT : $(openssl rand -hex 16)"
```

> **주의:** `PROVIDER_API_KEY_SALT`는 **반드시 hex 문자열**이어야 한다. Spring Security의 `Encryptors.delux()`가 hex로 디코드하기 때문. base64로 주면 `Detected a Non-hex character` 예외로 부팅 실패.

`.env` 내용:

```
MYSQL_ROOT_PASSWORD=<위에서 생성>
MYSQL_DATABASE=promptdeck
MYSQL_USER=promptdeck
MYSQL_PASSWORD=<위에서 생성>
MYSQL_PORT=3306

JWT_SECRET=<위에서 생성>
BACKEND_PORT=8080
PROVIDER_API_KEY_PASSWORD=<위에서 생성>
PROVIDER_API_KEY_SALT=<위 hex 값>

FRONTEND_PORT=3000
AUTH_ALLOWED_ORIGINS=https://promptdeck.duckdns.org
COMPOSE_PROFILES=https
```

HTTPS 구성(11장)에서는 Caddy가 80/443을 차지하므로 `FRONTEND_PORT`는 3000으로 둔다(방화벽이 3000을 막고 있어 외부 노출 없음). HTTPS 없이 IP로만 띄우는 경우 `FRONTEND_PORT=80`, `AUTH_ALLOWED_ORIGINS=http://<VM 외부 IP>`로 설정하고 `COMPOSE_PROFILES` 줄을 제거.

## 5. 실행

```bash
docker compose up -d --build
```

초기 빌드는 5~10분 소요 (Gradle 의존성 + Java 25 이미지 다운로드).

확인:

```bash
docker compose ps                    # 세 컨테이너 모두 Up
docker compose logs backend --tail=15  # "Started PromtDeckApplication" 확인
curl -I http://localhost             # 200 OK
```

## 6. JVM 메모리 튜닝

`e2-small`의 2GB RAM에서 안정 동작을 위해 백엔드 Dockerfile에 다음 옵션이 들어 있다:

```dockerfile
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-XX:+UseSerialGC", "-jar", "app.jar"]
```

- `-Xmx512m`: 힙 최대 512MB
- `-Xms256m`: 초기 힙 256MB
- `-XX:+UseSerialGC`: 단일 코어 친화적 GC

VM 사양을 올리는 경우 이 값을 늘려도 무방.

## 7. 비용 관리

`e2-small`은 시간당 약 $0.025. 24시간 가동 시 월 약 $18 + 디스크 $1 + IP $3.6 ≈ **$22/월**.

학교 프로젝트는 시연/개발 시에만 가동하고 그 외엔 VM을 정지하는 것이 효율적:

```bash
# GCP 콘솔 → VM 인스턴스 → 중지
# 또는 로컬에서:
gcloud compute instances stop promptdeck-vm --zone=asia-northeast3-a
gcloud compute instances start promptdeck-vm --zone=asia-northeast3-a
```

정지 중에는 컴퓨팅·IP 요금 0, 디스크 20GB만 월 약 $0.80 부과.

## 8. 재기동 시 주의

Ephemeral IP는 VM 시작마다 바뀐다. HTTPS 구성(11장) 사용 시 도메인이 고정 주소 역할을 하므로 `.env`는 건드릴 필요 없고, **DuckDNS에 새 IP만 알려주면 된다**:

```bash
curl -s "https://www.duckdns.org/update?domains=promptdeck&token=<DuckDNS 토큰>&ip=$(curl -s ifconfig.me)"
# 응답 OK 확인. 이후 https://promptdeck.duckdns.org 로 접속
```

(아래 12장의 cron을 등록해두면 이 작업도 자동화된다.)

HTTPS 없이 IP로만 운영하는 경우에는 기존 방식대로 `AUTH_ALLOWED_ORIGINS`을 갱신:

```bash
cd ~/PromptDeck
NEW_IP=$(curl -s ifconfig.me)
sed -i "s|AUTH_ALLOWED_ORIGINS=.*|AUTH_ALLOWED_ORIGINS=http://$NEW_IP|" .env
docker compose up -d backend
echo "새 주소: http://$NEW_IP"
```

컨테이너는 `restart: unless-stopped` 정책이라 VM 부팅 시 자동 기동.

## 9. 트러블슈팅

| 증상 | 원인 | 해결 |
| --- | --- | --- |
| 브라우저 접속 시 연결 실패 | GCP 방화벽 미설정 | VM 수정 → 네트워킹 → HTTP 트래픽 허용 체크 |
| `502 Bad Gateway` | 백엔드 부팅 실패 | `docker compose logs backend` 확인 |
| `Detected a Non-hex character` | `PROVIDER_API_KEY_SALT`가 hex 아님 | `openssl rand -hex 16`으로 재생성 |
| 로그인 후 401/CORS | `AUTH_ALLOWED_ORIGINS`과 접속 IP 불일치 | `.env` 갱신 후 `docker compose up -d backend` |
| 백엔드 `Restarting` 반복 | OOM | `-Xmx` 값을 384m 등으로 축소 |

## 10. 정리

과제 제출/평가 완료 후 VM을 삭제하면 모든 비용이 멈춘다:

- GCP 콘솔 → VM 인스턴스 → 삭제 (디스크도 함께 제거)
- 결제 보고서에서 잔여 청구 확인

## 11. HTTPS (DuckDNS + Caddy)

`https://promptdeck.duckdns.org` 로 서비스하는 구성. 도메인·인증서 모두 무료.

```
브라우저 → 방화벽(80,443) → Caddy(인증서 자동 발급/갱신, TLS 종료)
                              └→ nginx(frontend) → Spring Boot → MySQL
```

- **도메인**: [DuckDNS](https://www.duckdns.org)에 `promptdeck` 서브도메인 등록, IP는 VM 외부 IP로 설정 (계정: 팀 공용 확인)
- **방화벽**: VM에 `http-server`, `https-server` 태그 (80은 Let's Encrypt 발급 검증에 필수)
- **컨테이너**: `docker-compose.yml`의 `caddy` 서비스가 담당. `profiles: [https]`로 묶여 있어 `.env`에 `COMPOSE_PROFILES=https`가 있는 VM에서만 기동된다. **로컬 개발 환경에는 영향 없음.**
- **설정 파일**: 리포 루트의 `Caddyfile` (도메인 변경 시 이 파일 수정)
- 인증서는 `caddy-data` 볼륨에 보관되어 컨테이너를 재생성해도 재발급되지 않는다 (Let's Encrypt는 발급 횟수 제한이 있으므로 볼륨을 지우지 말 것).

문제 발생 시 `docker logs promptdeck-caddy`에서 인증서 발급 로그 확인.

## 12. DuckDNS 자동 갱신 (cron)

VM IP가 바뀌어도 도메인이 따라오도록 VM에 cron 등록:

```bash
crontab -e
# 아래 한 줄 추가 (5분마다 현재 IP를 DuckDNS에 보고)
*/5 * * * * curl -s "https://www.duckdns.org/update?domains=promptdeck&token=<DuckDNS 토큰>&ip=" >/dev/null 2>&1
```

`ip=` 를 비워두면 DuckDNS가 요청 발신 IP(=VM IP)를 자동 인식한다.
