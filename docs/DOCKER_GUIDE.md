# PromptDeck Docker 실행 가이드

PromptDeck은 Docker Compose를 사용해 로컬 개발 환경을 구성합니다.
이 문서는 처음 환경을 설정하는 팀원을 위한 가이드입니다.

## 1. 사전 준비

다음이 설치되어 있어야 합니다.

- Docker Desktop (Windows / macOS) 또는 Docker Engine (Linux)
- Git

Docker Desktop을 사용하는 경우, **Settings → Resources → WSL Integration**에서
사용 중인 WSL 배포판을 활성화해야 합니다.

설치 확인:

\`\`\`bash
docker --version
docker compose version
\`\`\`

## 2. 환경 변수 설정

저장소를 clone한 직후에는 \`.env\` 파일이 없습니다.
\`.env.example\`을 복사해서 \`.env\`로 만든 뒤 값을 채워 주세요.

\`\`\`bash
cp .env.example .env
\`\`\`

기본값 그대로 두어도 로컬 개발에는 문제가 없지만,
공유 환경이나 외부 접근 환경에서는 비밀번호를 반드시 변경해야 합니다.

\`.env\` 파일은 \`.gitignore\`에 의해 Git 추적에서 제외됩니다.
민감한 비밀번호가 저장소에 올라가지 않도록 주의해 주세요.

### 포트 충돌 시

\`MYSQL_PORT=3306\`이 이미 사용 중이라면(예: 로컬에 MySQL이 설치되어 있는 경우)
\`.env\`의 \`MYSQL_PORT\`를 다른 값(예: 3307)으로 변경해 주세요.

## 3. 컨테이너 실행

프로젝트 최상위(\`docker-compose.yml\`이 있는 폴더)에서:

\`\`\`bash
docker compose up -d
\`\`\`

\`-d\` 옵션은 백그라운드 실행을 의미합니다.

상태 확인:

\`\`\`bash
docker ps
\`\`\`

\`promptdeck-mysql\` 컨테이너의 STATUS가 \`Up X seconds (healthy)\`로 표시되면 정상입니다.
처음 실행 시 healthcheck가 \`healthy\`로 바뀌기까지 약 30초가 소요될 수 있습니다.

## 4. MySQL 접속 확인

컨테이너에 접속해 데이터베이스가 정상적으로 만들어졌는지 확인할 수 있습니다.

\`\`\`bash
docker exec -it promptdeck-mysql mysql -u promptdeck -p
\`\`\`

비밀번호는 \`.env\`의 \`MYSQL_PASSWORD\` 값을 입력합니다.

접속 후:

\`\`\`sql
SHOW DATABASES;
\`\`\`

목록에 \`promptdeck\` 데이터베이스가 보이면 정상입니다.

종료:

\`\`\`sql
exit
\`\`\`

## 5. 컨테이너 정리

작업이 끝났을 때 컨테이너를 멈추려면:

\`\`\`bash
docker compose down
\`\`\`

이 명령은 컨테이너와 네트워크를 제거하지만,
\`mysql-data\` 볼륨은 유지되므로 다음 실행 시 데이터가 그대로 남아 있습니다.

볼륨까지 완전히 삭제하려면(데이터 초기화):

\`\`\`bash
docker compose down -v
\`\`\`

## 6. 자주 묻는 질문

### Q. \`port already in use\` 에러가 납니다.

로컬에 이미 MySQL이 실행 중이거나 다른 프로그램이 3306 포트를 사용 중일 수 있습니다.
\`.env\`의 \`MYSQL_PORT\`를 다른 값으로 변경하거나, 기존 MySQL 서비스를 중지해 주세요.

### Q. 컨테이너가 \`unhealthy\` 상태입니다.

\`docker compose logs mysql\` 명령으로 로그를 확인해 주세요.
대부분의 경우 환경 변수 설정 문제이거나 MySQL 초기화 시간이 부족한 경우입니다.

### Q. 데이터를 초기화하고 다시 시작하고 싶습니다.

\`docker compose down -v\` 후 \`docker compose up -d\`를 실행하면
볼륨이 새로 생성되어 빈 상태로 시작됩니다.

## 7. 향후 추가 예정

- 백엔드(Spring Boot) 컨테이너
- 프론트엔드(React + Vite) 컨테이너

백엔드/프론트엔드 코드가 추가되는 시점에 \`docker-compose.yml\`에 서비스를 더하고
이 문서에도 실행 방법을 보강할 예정입니다.
