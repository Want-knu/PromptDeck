# PromptDeck

PromptDeck은 사용자가 자신의 로컬 환경에서 실행하는 LLM API 요청 빌더입니다.

사용자는 OpenAI, Gemini, Claude, Custom API 같은 Provider별 요청 형식을 설정하고, 자주 사용하는 프롬프트 프리셋을 저장한 뒤, 선택한 Provider로 요청을 전송해 응답 결과와 기록을 확인할 수 있습니다.

이 저장소는 현재 과제 수행을 위한 초기 기획 단계에 있으며, 팀 논의를 거쳐 세부 기술 스택과 구현 범위가 변경될 수 있습니다.

## 핵심 방향

- 중앙 서버형 SaaS가 아닌 로컬 실행형 웹 애플리케이션
- 사용자의 API Key를 외부 서버에 저장하지 않는 구조
- Provider별 요청 JSON 템플릿 관리
- 프롬프트 프리셋 저장 및 재사용
- 요청/응답 기록 저장
- Docker 기반 실행 환경 제공
- GitHub Issues, Pull Requests, Projects를 활용한 협업 관리

## 초기 기술 방향

- Frontend: React + Vite
- Backend: Flask
- Database: SQLite
- Containerization: Docker, Docker Compose
- CI/CD: GitHub Actions

## 문서

- [프로젝트 계획서](docs/PROJECT_PLAN.md)
- [기술 선택 근거](docs/TECH_DECISIONS.md)
- [협업 운영 계획](docs/COLLABORATION_PLAN.md)
- [제출 계획 보강 문서](docs/SUBMISSION_READINESS.md)

## 과제 요구사항 대응

PromptDeck은 Flask 기반 REST API, SQLite 데이터베이스, Docker 실행 환경, GitHub Actions 기반 CI/CD, GitHub Issues/PR/Projects 협업 방식을 통해 과제의 주요 기술 요구사항을 충족하는 것을 목표로 합니다.
