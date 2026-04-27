# PromptDeck 프로젝트 계획서

> 상태: 초기 기획안  
> 목적: 팀 논의 및 과제 계획서 작성을 위한 기준 문서  
> 비고: 세부 구현 범위와 기술 선택은 팀 협의 후 변경될 수 있다.

## 관련 문서

- [기술 선택 근거](TECH_DECISIONS.md)
- [협업 운영 계획](COLLABORATION_PLAN.md)
- [제출 계획 보강 문서](SUBMISSION_READINESS.md)

## 1. 프로젝트 개요

PromptDeck은 사용자가 로컬 환경에서 실행하는 LLM API 요청 빌더이다.

사용자는 OpenAI, Gemini, Claude, Custom API 등 여러 LLM Provider의 요청 형식을 설정하고, 자주 사용하는 프롬프트 프리셋을 저장한 뒤, 선택한 Provider로 요청을 실행할 수 있다. 실행 결과와 요청 기록은 로컬 데이터베이스에 저장하여 다시 확인할 수 있다.

본 프로젝트는 중앙 서버를 운영하는 SaaS가 아니라, 각 사용자가 자신의 PC 또는 로컬 Docker 환경에서 실행하는 개인용 도구를 목표로 한다. 따라서 MVP에서는 회원가입/로그인 기능을 제외하고, 로컬 데이터 관리와 API 요청 빌더 기능에 집중한다.

## 2. 프로젝트 배경

LLM API는 Provider마다 endpoint, header, request body, response format이 다르다. 사용자가 여러 Provider를 테스트하거나 프롬프트를 반복적으로 실험하려면 매번 JSON 요청을 직접 작성해야 하며, 자주 사용하는 프롬프트를 체계적으로 관리하기도 어렵다.

PromptDeck은 이러한 문제를 줄이기 위해 다음 기능을 제공한다.

- Provider별 요청 형식 관리
- 프롬프트 프리셋 저장 및 재사용
- API 요청 JSON 미리보기
- 실제 LLM API 요청 실행
- 요청/응답 기록 저장
- Custom API 확장을 통한 유연한 테스트 환경 제공

## 3. 프로젝트 목표

### 3.1 기능적 목표

- 사용자가 LLM Provider 설정을 등록, 조회, 수정, 삭제할 수 있다.
- 사용자가 프롬프트 프리셋을 등록, 조회, 수정, 삭제할 수 있다.
- 사용자가 선택한 Provider와 프리셋을 조합해 API 요청을 생성할 수 있다.
- 생성된 요청 JSON을 실행 전에 확인할 수 있다.
- 사용자가 입력한 API Key를 로컬 환경에서 관리할 수 있다.
- 요청 결과와 오류 정보를 기록으로 저장할 수 있다.
- Custom API Provider를 통해 임의의 REST API 요청 형식을 등록할 수 있다.

### 3.2 비기능적 목표

- 과제 기간 안에 구현 가능한 범위로 MVP를 완성한다.
- REST API, DB, Docker, CI/CD 등 과제 기술 요구사항을 명확히 충족한다.
- 팀원이 역할을 나누기 쉬운 구조로 개발한다.
- 이후 개인 프로젝트로 확장 가능한 Provider Adapter 구조를 둔다.
- API Key가 GitHub 저장소에 올라가지 않도록 관리한다.

## 4. MVP 범위

MVP에서 구현할 기능은 다음과 같다.

- Provider 설정 관리
- 프롬프트 프리셋 관리
- 요청 JSON 빌더
- API Key 로컬 설정 파일 관리
- LLM 요청 실행
- 요청/응답 히스토리 저장
- 간단한 채팅형 테스트 화면
- Docker 기반 실행 환경
- GitHub Actions 기반 테스트/빌드 확인

MVP에서 제외할 기능은 다음과 같다.

- 회원가입/로그인
- 중앙 서버 배포
- 팀 단위 프리셋 공유
- Provider별 사용 요금 계산
- 실시간 협업 편집
- 고급 모니터링 대시보드

## 5. 주요 사용자 시나리오

### 5.1 Provider 설정

사용자는 OpenAI, Gemini, Claude, Custom API 중 하나를 선택하고, endpoint, model, header, body template 등 요청에 필요한 정보를 설정한다.

### 5.2 프롬프트 프리셋 생성

사용자는 번역, 요약, 코드 리뷰, 글쓰기 보조 등 반복적으로 사용하는 system prompt와 user prompt template을 프리셋으로 저장한다.

### 5.3 요청 생성 및 실행

사용자는 Provider와 프리셋을 선택하고 입력값을 작성한다. PromptDeck은 선택된 Provider의 요청 형식에 맞는 JSON을 생성하고, 사용자가 확인한 뒤 요청을 실행한다.

### 5.4 요청 기록 확인

사용자는 과거 요청의 Provider, 프리셋, 요청 본문, 응답 본문, 실행 성공 여부, 오류 메시지 등을 확인한다.

## 6. 시스템 구성

초기 구성은 다음과 같다.

```text
Frontend
  React + Vite
  - Provider 설정 화면
  - 프롬프트 프리셋 관리 화면
  - 요청 JSON 미리보기
  - 채팅/실행 화면
  - 요청 기록 화면

Backend
  Flask REST API
  - Provider API
  - Preset API
  - Request Builder API
  - Chat Execution API
  - History API
  - Local Secret Config API

Database
  SQLite
  - providers
  - prompt_presets
  - request_templates
  - chat_sessions
  - messages
  - execution_logs

Local Config
  secrets.local.json
  - Provider별 API Key
  - Git 추적 제외
```

## 7. 과제 요구사항 대응

| 요구사항 | PromptDeck 대응 방식 |
| --- | --- |
| GitHub Pull Requests | 기능 단위 브랜치와 PR 생성 |
| GitHub Issues | 기능, 버그, 문서 작업을 Issue로 관리 |
| GitHub Projects | Backlog, Todo, In Progress, Review, Done으로 진행 상황 관리 |
| Web Framework | Flask 기반 백엔드 구현 |
| REST API | Provider, Preset, History, Execution API 제공 |
| Database Integration | SQLite 기반 로컬 데이터 저장 |
| Containerization | Dockerfile 및 Docker Compose 구성 |
| DevOps | GitHub Actions로 테스트/빌드 자동화 |
| Cloud Environment | MVP에서는 제외, 추후 선택 확장 |
| Monitoring | MVP에서는 제외, 추후 로그/메트릭 확장 가능 |

## 8. 데이터 저장 정책

- 프롬프트 프리셋, Provider 설정, 요청 기록은 SQLite에 저장한다.
- API Key는 외부 서버에 저장하지 않는다.
- API Key는 로컬 설정 파일에 저장하고 Git 추적에서 제외한다.
- 실제 API Key가 커밋되지 않도록 `.gitignore`와 예시 설정 파일을 분리한다.
- 추후 확장 시 OS Keychain 또는 암호화 저장 방식을 검토한다.

## 9. 예상 일정

| 단계 | 주요 작업 |
| --- | --- |
| 1단계 | 프로젝트 구조 생성, README/docs 작성, 협업 규칙 정리 |
| 2단계 | Flask REST API 기본 구조 및 SQLite 모델 구현 |
| 3단계 | React 화면 구조 및 Provider/Preset 관리 UI 구현 |
| 4단계 | 요청 JSON 빌더 및 LLM 요청 실행 기능 구현 |
| 5단계 | 요청/응답 히스토리, 오류 처리, Docker 구성 |
| 6단계 | GitHub Actions, 테스트, 문서 정리, 발표 준비 |

## 10. 주요 리스크와 대응

| 리스크 | 대응 |
| --- | --- |
| Provider별 API 형식 차이가 큼 | Adapter 구조로 Provider별 변환 로직 분리 |
| API Key 노출 위험 | 로컬 저장, Git 추적 제외, 예시 파일 분리 |
| 기능 범위 과다 | MVP와 확장 기능을 명확히 분리 |
| 팀원 간 역할 중복 | Issues와 Projects로 작업 단위 분리 |
| Docker/CI 설정 지연 | 초기부터 최소 구성으로 시작 후 점진 개선 |

## 11. 향후 확장 가능성

- Provider 추가: Ollama, OpenRouter, LM Studio 등
- 프리셋 버전 관리
- 프리셋 import/export
- 요청 비용 및 토큰 사용량 추정
- 로컬 모델 실행 지원
- OS Keychain 연동
- Electron 또는 Tauri 기반 데스크톱 앱 패키징
- Grafana 등 모니터링 도구 연동
