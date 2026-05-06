# PromptDeck

PromptDeck은 GCP 서버에 배포되는 LLM API 요청 빌더 웹 서비스입니다.

사용자는 로그인 후 OpenAI, Gemini, Claude, Custom API 같은 Provider별 요청 형식을 설정하고, 자주 사용하는 프롬프트 프리셋을 저장한 뒤, 선택한 Provider로 요청을 전송해 응답 결과와 기록을 확인할 수 있습니다.

## 핵심 방향

- GCP 기반 서버 배포형 웹 서비스
- 로그인 기능과 사용자별 데이터 분리
- Provider별 요청 JSON 템플릿 관리
- 프롬프트 프리셋 저장 및 재사용
- 요청/응답 기록 저장
- Provider API Key 보안 저장, 마스킹 표시, 삭제 지원
- Docker 기반 실행 환경 제공
- GitHub Issues, Pull Requests, Projects를 활용한 협업 관리

## 확정 기술 스택

- Frontend: React + Vite
- Backend: Java Spring REST API Server
- Database: MySQL
- DB Access: JPA / JDBC
- Deployment: GCP
- Containerization: Docker, Docker Compose
- CI/CD: GitHub Actions

## 문서

- [프로젝트 제안서](docs/PROJECT_PROPOSAL.md)
- [프로젝트 계획서](docs/PROJECT_PLAN.md)
- [협업 운영 계획](docs/COLLABORATION_PLAN.md)
- [시스템 아키텍처 다이어그램](docs/system_architecture.drawio)

## 과제 요구사항 대응

PromptDeck은 Java Spring 기반 REST API, MySQL 데이터베이스, Docker 실행 환경, GCP 서버 배포, GitHub Actions 기반 CI/CD, GitHub Issues/PR/Projects 협업 방식을 통해 과제의 주요 기술 요구사항을 충족하는 것을 목표로 합니다.
