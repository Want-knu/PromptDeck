# PromptDeck Docker 실행 가이드

PromptDeck은 Docker Compose로 MySQL, Spring Boot 백엔드, React/nginx 프론트엔드를 함께 실행합니다.
이 문서는 처음 환경을 설정하는 팀원을 위한 로컬 실행 가이드입니다.

## 1. 사전 준비

다음이 설치되어 있어야 합니다.

- Docker Desktop (Windows / macOS) 또는 Docker Engine (Linux)
- Git

Docker Desktop을 사용하는 경우, **Settings -> Resources -> WSL Integration**에서 사용 중인 WSL 배포판을 활성화해야 합니다.

설치 확인:

```bash
docker --version
docker compose version
```

## 2. 환경 변수 설정

저장소를 clone한 직후에는 `.env` 파일이 없습니다. `.env.example`을 복사해서 `.env`로 만든 뒤 값을 채워 주세요.

```bash
cp .env.example .env
```

공유 환경이나 외부 접근 환경에서는 `.env.example`의 기본 비밀번호를 반드시 변경해야 합니다. `PROVIDER_API_KEY_SALT`는 hex 문자열이어야 합니다.

### 포트 충돌 시

`FRONTEND_PORT=3000`이 이미 사용 중이라면 `.env`의 `FRONTEND_PORT`를 다른 값으로 변경해 주세요. 백엔드와 MySQL은 Docker 내부 네트워크에서만 접근하므로 host 포트를 사용하지 않습니다.

## 3. 컨테이너 실행

프로젝트 최상위(`docker-compose.yml`이 있는 폴더)에서 실행합니다.

```bash
docker compose up -d --build
```

상태 확인:

```bash
docker compose ps
```

`promptdeck-mysql`, `promptdeck-backend`, `promptdeck-frontend`가 모두 `Up` 상태이고, `promptdeck-mysql`이 `healthy`로 표시되면 정상입니다.

브라우저에서는 다음 주소로 접속합니다.

```text
http://localhost:3000
```

`FRONTEND_PORT`를 변경했다면 해당 포트로 접속합니다. API 요청은 nginx가 `/api` 경로를 백엔드 컨테이너로 프록시합니다.

## 4. API 프록시 확인

로그인하지 않은 상태에서 보호 API를 호출했을 때 401 응답이 내려오면 nginx -> backend 프록시가 정상입니다.

```bash
curl -i http://localhost:3000/api/provider-keys
```

## 5. MySQL 접속 확인

컨테이너 내부에서 데이터베이스가 정상적으로 만들어졌는지 확인할 수 있습니다.

```bash
docker exec -it promptdeck-mysql mysql -u promptdeck -p
```

비밀번호는 `.env`의 `MYSQL_PASSWORD` 값을 입력합니다.

```sql
SHOW DATABASES;
```

목록에 `promptdeck` 데이터베이스가 보이면 정상입니다.

## 6. 컨테이너 정리

작업이 끝났을 때 컨테이너를 멈추려면:

```bash
docker compose down
```

이 명령은 컨테이너와 네트워크를 제거하지만, `mysql-data` 볼륨은 유지되므로 다음 실행 시 데이터가 그대로 남아 있습니다.

볼륨까지 완전히 삭제하려면:

```bash
docker compose down -v
```

## 7. 자주 묻는 질문

### Q. `port already in use` 에러가 납니다.

다른 프로그램이 프론트엔드 포트 3000을 사용 중일 수 있습니다. `.env`의 `FRONTEND_PORT`를 다른 값으로 변경하거나 기존 서비스를 중지해 주세요.

### Q. 컨테이너가 `unhealthy` 상태입니다.

`docker compose logs mysql` 명령으로 로그를 확인해 주세요. 대부분의 경우 환경 변수 설정 문제이거나 MySQL 초기화 시간이 부족한 경우입니다.

### Q. 데이터를 초기화하고 다시 시작하고 싶습니다.

`docker compose down -v` 후 `docker compose up -d --build`를 실행하면 볼륨이 새로 생성되어 빈 상태로 시작됩니다.
