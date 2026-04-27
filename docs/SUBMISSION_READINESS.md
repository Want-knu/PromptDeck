# PromptDeck 제출 계획 보강 문서

> 상태: 팀 논의용 초안
> 목적: 계획서 제출 전 확정해야 할 사항과 제출 결과물에 남길 내용을 정리한다.
> 사용 방식: 내일 팀 회의에서 결정 사항을 체크하고, 결정된 내용은 `PROJECT_PLAN.md`, `TECH_DECISIONS.md`, `COLLABORATION_PLAN.md`에 반영한다.

## 1. 제출 계획서의 목표

계획서는 읽는 사람이 다음 질문에 답할 수 있어야 한다.

- 무엇을 만드는가?
- 왜 만드는가?
- 어떤 기술을 사용하는가?
- 어떤 기능을 MVP로 구현하는가?
- 어떤 기능은 제외하거나 추후 확장으로 남기는가?
- 팀원은 어떻게 역할을 나누는가?
- GitHub Issues, Pull Requests, Projects를 어떻게 활용하는가?
- Docker와 CI/CD는 어떤 수준까지 구현하는가?
- 최종 산출물은 무엇인가?

PromptDeck의 제출 계획서는 단순 아이디어 설명이 아니라, 과제 요구사항을 어떻게 만족할지 보여주는 실행 계획이어야 한다.

## 2. 현재 확정해도 좋은 결정 사항

아래 항목은 현재 프로젝트 방향과 과제 요구사항을 고려했을 때 확정안으로 두는 것이 적절하다.

| 항목 | 결정안 | 근거 |
| --- | --- | --- |
| 프로젝트명 | PromptDeck | 프롬프트와 API 요청 구성을 카드/덱처럼 관리한다는 의미가 명확하다. |
| 프로젝트 유형 | 로컬 실행형 LLM API 요청 빌더 | API Key를 외부 서버에 저장하지 않는 구조를 설명하기 좋다. |
| 서비스 형태 | 중앙 서버형 SaaS가 아닌 개인 로컬 도구 | 로그인 없이도 설계 의도가 자연스럽다. |
| 핵심 기능 | Provider 설정, 프리셋, 요청 JSON 빌더, 요청 실행, 히스토리 | REST API, DB, UI 구현 범위가 명확하다. |
| Provider 범위 | OpenAI, Gemini, Claude, Custom API | 대표 Provider와 확장성을 함께 보여줄 수 있다. |
| 로그인 | MVP에서 제외 | 로컬 단일 사용자 도구이므로 핵심 기능 우선 구현이 타당하다. |
| API Key 저장 | 로컬 설정 파일 저장, Git 추적 제외 | 중앙 서버 저장 회피와 구현 난이도 사이의 균형이 좋다. |
| Backend | Flask | 과제 요구사항에 맞고 REST API MVP 구현 난이도가 낮다. |
| Frontend | React + Vite | 동적 폼, JSON 미리보기, 채팅 UI 구현에 적합하다. |
| Database | SQLite | 로컬 실행형 프로그램과 잘 맞고 DB 연동 요구사항을 충족한다. |
| Container | Docker, Docker Compose | 로컬 실행 환경을 재현 가능하게 만든다. |
| CI/CD | GitHub Actions | PR 단위 테스트/빌드 자동화로 DevOps 요구사항을 충족한다. |

## 3. 팀 회의에서 확정해야 할 사항

아래 항목은 계획서 제출 전 팀원들과 합의하는 것이 좋다.

### 3.1 구현 우선순위

결정할 내용:

- MVP에서 실제 요청 실행을 어디까지 구현할지
- Provider별 Adapter를 모두 구현할지, 일부는 템플릿만 제공할지
- 채팅 화면과 JSON 빌더 중 어느 쪽을 더 강조할지

권장안:

- Custom API와 OpenAI-compatible 요청 실행을 우선 구현한다.
- Gemini와 Claude는 Provider 타입, 설정 구조, 요청 템플릿을 먼저 마련한다.
- 시간이 허용되면 Gemini 또는 Claude 중 하나를 실제 요청 실행까지 확장한다.

이유:

- 3사 API를 모두 완성하려고 하면 일정 위험이 커진다.
- Custom API가 있으면 다른 Provider도 수동으로 테스트할 수 있다.
- Adapter 구조를 만들면 확장성은 계획서와 코드에서 모두 설명할 수 있다.

### 3.2 API Key 저장 수준

결정할 내용:

- 로컬 설정 파일에 평문 저장할지
- MVP에서 간단한 마스킹/삭제 기능까지만 제공할지
- 암호화 저장을 MVP에 포함할지

권장안:

- MVP에서는 `config/secrets.local.json`에 저장한다.
- `config/secrets.example.json`만 저장소에 포함한다.
- `secrets.local.json`은 `.gitignore`에 등록한다.
- UI에서는 API Key 입력값을 마스킹하고, 삭제 기능을 제공한다.

이유:

- 과제용 MVP에서 암호화 저장까지 구현하면 핵심 기능 일정이 밀릴 수 있다.
- 외부 서버에 저장하지 않는다는 보안 방향은 충분히 설명 가능하다.
- 추후 OS Keychain 또는 암호화 저장으로 확장할 수 있다.

### 3.3 Docker 구성 범위

결정할 내용:

- Frontend와 Backend를 각각 컨테이너로 분리할지
- Backend가 React build 결과를 정적 파일로 서빙할지
- SQLite 파일과 설정 파일을 어떤 볼륨에 저장할지

권장안:

- 개발 단계는 `frontend`, `backend` 컨테이너를 분리한다.
- SQLite와 로컬 설정 파일은 Docker volume 또는 bind mount로 유지한다.
- 최종 제출에서는 `docker compose up`으로 실행 가능한 구성을 제공한다.

이유:

- 프론트엔드/백엔드 역할 분담이 쉽다.
- Docker 요구사항을 명확히 보여줄 수 있다.
- DB 서버 컨테이너 없이도 로컬 앱 구조를 유지할 수 있다.

### 3.4 CI/CD 범위

결정할 내용:

- PR마다 어떤 검사를 자동 실행할지
- Docker build 검증을 포함할지
- 실제 배포를 할지

권장안:

- Pull Request마다 backend test, frontend build, Docker build 검증을 실행한다.
- 실제 클라우드 배포는 MVP에서 제외한다.

이유:

- 과제 DevOps 요구사항을 충족한다.
- 로컬 실행형 도구라는 프로젝트 방향과 맞다.
- 클라우드 계정, 비용, 비밀값 관리 부담을 줄일 수 있다.

### 3.5 팀원 역할 분담

결정할 내용:

- 팀원별 주 담당 영역
- PR 리뷰 방식
- 문서와 발표 담당

권장 역할:

| 역할 | 주요 책임 |
| --- | --- |
| Frontend | React 화면, JSON 빌더 UI, 채팅/히스토리 UI |
| Backend | Flask REST API, Provider Adapter, 요청 실행 로직 |
| Database | SQLite 모델, 저장 구조, 히스토리 관리 |
| DevOps | Docker Compose, GitHub Actions, 실행 가이드 |
| Documentation | 계획서, README, API 문서, 발표 자료 |

소규모 팀이라면 한 사람이 여러 역할을 맡되, Issue 단위로 책임자를 명확히 두는 것이 좋다.

## 4. 계획서에 반드시 남길 결과물

제출 계획서에는 다음 항목이 들어가야 한다.

| 항목 | 포함해야 할 내용 |
| --- | --- |
| 프로젝트 개요 | PromptDeck의 정의와 로컬 실행형 도구라는 방향 |
| 문제 정의 | LLM Provider마다 요청 형식이 다르고 프롬프트 재사용이 불편하다는 문제 |
| 프로젝트 목표 | 요청 빌더, 프리셋, 히스토리, 로컬 API Key 관리 |
| 주요 기능 | Provider 관리, 프리셋 CRUD, 요청 JSON 미리보기, 요청 실행, 히스토리 |
| MVP 범위 | 제출 시점까지 반드시 구현할 기능 |
| 제외 범위 | 로그인, 중앙 서버, 클라우드 배포, 고급 모니터링 등 |
| 기술 스택 | React + Vite, Flask, SQLite, Docker, GitHub Actions |
| 기술 선택 이유 | 구현 난이도, 과제 요구사항, 로컬 실행형 방향과의 적합성 |
| 시스템 구조 | Frontend, Backend, SQLite, Local Config 구성 |
| REST API 초안 | 주요 endpoint와 역할 |
| DB 설계 초안 | 주요 테이블과 저장 데이터 |
| 협업 계획 | Issues, PR, Projects, 브랜치 전략 |
| 일정 계획 | 단계별 개발 일정 |
| 역할 분담 | 팀원별 담당 영역 |
| 리스크 대응 | API Key, 범위 과다, Provider 차이, Docker/CI 지연 대응 |
| 최종 산출물 | GitHub repo, README, 실행 가이드, Docker, CI, 발표 자료 |

## 5. MVP 완료 기준

MVP는 다음 조건을 만족하면 완료로 본다.

- 사용자가 Provider 설정을 생성, 조회, 수정, 삭제할 수 있다.
- 사용자가 프롬프트 프리셋을 생성, 조회, 수정, 삭제할 수 있다.
- 사용자가 Provider와 프리셋을 선택해 요청 JSON을 생성할 수 있다.
- 생성된 요청 JSON을 실행 전에 확인할 수 있다.
- 사용자가 입력한 API Key를 로컬 설정 파일에 저장하고 불러올 수 있다.
- 최소 1개 이상의 Provider 또는 Custom API로 실제 요청을 실행할 수 있다.
- 요청 결과와 오류 정보가 SQLite에 저장된다.
- 히스토리 화면에서 과거 요청과 응답을 확인할 수 있다.
- Docker Compose로 프로젝트를 실행할 수 있다.
- GitHub Actions에서 기본 테스트/빌드 검증이 동작한다.
- README에 실행 방법과 주요 기능 설명이 정리되어 있다.

## 6. REST API 설계 초안

아래 API는 MVP 기준의 초안이다. 실제 구현 중 세부 경로와 request/response 형식은 조정될 수 있다.

### 6.1 Provider API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/providers` | 등록된 Provider 목록 조회 |
| POST | `/api/providers` | Provider 설정 생성 |
| GET | `/api/providers/{provider_id}` | Provider 상세 조회 |
| PUT | `/api/providers/{provider_id}` | Provider 설정 수정 |
| DELETE | `/api/providers/{provider_id}` | Provider 삭제 |

Provider 예시 필드:

- `name`
- `type`
- `base_url`
- `default_model`
- `headers_template`
- `body_template`
- `response_path`
- `enabled`

### 6.2 Secret Config API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/secrets/providers` | API Key 저장 여부 조회 |
| PUT | `/api/secrets/providers/{provider_type}` | Provider별 API Key 저장/수정 |
| DELETE | `/api/secrets/providers/{provider_type}` | Provider별 API Key 삭제 |

주의:

- 응답에는 실제 API Key를 그대로 반환하지 않는다.
- 저장 여부, 마지막 수정일, 마스킹된 값 정도만 반환한다.

### 6.3 Prompt Preset API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/presets` | 프롬프트 프리셋 목록 조회 |
| POST | `/api/presets` | 프롬프트 프리셋 생성 |
| GET | `/api/presets/{preset_id}` | 프리셋 상세 조회 |
| PUT | `/api/presets/{preset_id}` | 프리셋 수정 |
| DELETE | `/api/presets/{preset_id}` | 프리셋 삭제 |

Preset 예시 필드:

- `title`
- `description`
- `category`
- `system_prompt`
- `user_prompt_template`
- `variables`
- `tags`

### 6.4 Request Builder API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| POST | `/api/requests/preview` | Provider와 프리셋을 조합해 요청 JSON 미리보기 생성 |
| POST | `/api/requests/execute` | 실제 API 요청 실행 |

Preview 요청 예시:

```json
{
  "provider_id": 1,
  "preset_id": 2,
  "model": "gpt-4.1-mini",
  "variables": {
    "topic": "Docker",
    "tone": "beginner-friendly"
  },
  "user_input": "Docker Compose를 쉽게 설명해줘."
}
```

Preview 응답 예시:

```json
{
  "provider": "OpenAI",
  "method": "POST",
  "url": "https://api.openai.com/v1/chat/completions",
  "headers_preview": {
    "Authorization": "Bearer ***",
    "Content-Type": "application/json"
  },
  "body": {
    "model": "gpt-4.1-mini",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "Docker Compose를 쉽게 설명해줘."
      }
    ]
  }
}
```

### 6.5 History API

| Method | Endpoint | 설명 |
| --- | --- | --- |
| GET | `/api/history` | 요청 실행 기록 목록 조회 |
| GET | `/api/history/{history_id}` | 요청 실행 기록 상세 조회 |
| DELETE | `/api/history/{history_id}` | 요청 실행 기록 삭제 |

History 예시 필드:

- `provider_name`
- `preset_title`
- `request_body`
- `response_body`
- `status`
- `status_code`
- `error_message`
- `latency_ms`
- `created_at`

## 7. DB 설계 초안

SQLite 기준의 초기 테이블 구성이다.

### 7.1 `providers`

Provider 설정을 저장한다.

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `id` | integer | Primary key |
| `name` | text | 사용자가 지정한 Provider 이름 |
| `type` | text | `openai`, `gemini`, `claude`, `custom` |
| `base_url` | text | API endpoint base URL |
| `default_model` | text | 기본 모델명 |
| `headers_template` | text | JSON 문자열 형태의 header template |
| `body_template` | text | JSON 문자열 형태의 body template |
| `response_path` | text | 응답 본문에서 결과 텍스트를 찾는 경로 |
| `enabled` | integer | 사용 여부 |
| `created_at` | datetime | 생성일 |
| `updated_at` | datetime | 수정일 |

### 7.2 `prompt_presets`

프롬프트 프리셋을 저장한다.

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `id` | integer | Primary key |
| `title` | text | 프리셋 이름 |
| `description` | text | 프리셋 설명 |
| `category` | text | 번역, 요약, 코드 리뷰 등 |
| `system_prompt` | text | System prompt |
| `user_prompt_template` | text | 변수 치환이 가능한 user prompt template |
| `variables` | text | JSON 문자열 형태의 변수 정의 |
| `tags` | text | JSON 문자열 형태의 태그 목록 |
| `created_at` | datetime | 생성일 |
| `updated_at` | datetime | 수정일 |

### 7.3 `chat_sessions`

요청 실행 묶음을 저장한다.

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `id` | integer | Primary key |
| `title` | text | 세션 제목 |
| `provider_id` | integer | 사용한 Provider |
| `preset_id` | integer | 사용한 프리셋 |
| `created_at` | datetime | 생성일 |
| `updated_at` | datetime | 수정일 |

### 7.4 `messages`

채팅형 실행 화면의 메시지를 저장한다.

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `id` | integer | Primary key |
| `session_id` | integer | 연결된 채팅 세션 |
| `role` | text | `system`, `user`, `assistant`, `tool`, `error` |
| `content` | text | 메시지 내용 |
| `metadata` | text | JSON 문자열 형태의 부가 정보 |
| `created_at` | datetime | 생성일 |

### 7.5 `execution_logs`

실제 API 요청 실행 기록을 저장한다.

| 컬럼 | 타입 | 설명 |
| --- | --- | --- |
| `id` | integer | Primary key |
| `session_id` | integer | 연결된 채팅 세션 |
| `provider_id` | integer | 사용한 Provider |
| `preset_id` | integer | 사용한 프리셋 |
| `request_url` | text | 요청 URL |
| `request_headers` | text | 마스킹된 요청 header |
| `request_body` | text | 요청 body |
| `response_body` | text | 응답 body |
| `status` | text | `success`, `failed` |
| `status_code` | integer | HTTP status code |
| `error_message` | text | 오류 메시지 |
| `latency_ms` | integer | 응답 시간 |
| `created_at` | datetime | 생성일 |

### 7.6 로컬 설정 파일

API Key는 DB가 아니라 로컬 설정 파일에 저장한다.

예상 위치:

```text
config/secrets.local.json
```

예시:

```json
{
  "openai": {
    "api_key": "sk-..."
  },
  "gemini": {
    "api_key": "..."
  },
  "claude": {
    "api_key": "..."
  }
}
```

저장소에는 다음 예시 파일만 포함한다.

```text
config/secrets.example.json
```

## 8. 화면 구성 초안

MVP 화면은 다음 정도로 구성한다.

| 화면 | 주요 기능 |
| --- | --- |
| Dashboard | 최근 실행 기록, 등록 Provider, 프리셋 수 요약 |
| Provider Settings | Provider 등록/수정/삭제, API Key 저장 상태 확인 |
| Prompt Presets | 프리셋 목록, 생성, 수정, 삭제 |
| Request Builder | Provider와 프리셋 선택, 변수 입력, JSON 미리보기 |
| Chat/Test Runner | 요청 실행, 응답 표시, 오류 표시 |
| History | 과거 요청/응답 기록 조회 |

우선순위:

1. Provider Settings
2. Prompt Presets
3. Request Builder
4. Chat/Test Runner
5. History
6. Dashboard

Dashboard는 시간이 부족하면 간단한 요약 화면으로 축소한다.

## 9. GitHub Issue 초안

내일 회의 후 바로 등록할 수 있는 Issue 후보이다.

### 9.1 Documentation

- README 프로젝트 개요 정리
- 프로젝트 계획서 제출용 문서 정리
- 기술 선택 근거 문서 보강
- API/DB 설계 문서 작성

### 9.2 Backend

- Flask 프로젝트 구조 생성
- SQLite 연결 및 기본 설정 구성
- Provider 모델 및 CRUD API 구현
- Prompt Preset 모델 및 CRUD API 구현
- Local Secret Config API 구현
- Request Preview API 구현
- Request Execute API 구현
- History API 구현
- Provider Adapter 인터페이스 구현

### 9.3 Frontend

- React + Vite 프로젝트 구조 생성
- 기본 레이아웃 및 라우팅 구성
- Provider Settings 화면 구현
- Prompt Presets 화면 구현
- Request Builder 화면 구현
- Chat/Test Runner 화면 구현
- History 화면 구현
- API 클라이언트 모듈 작성

### 9.4 DevOps

- Backend Dockerfile 작성
- Frontend Dockerfile 작성
- Docker Compose 구성
- SQLite 데이터 볼륨 설정
- GitHub Actions backend test workflow 작성
- GitHub Actions frontend build workflow 작성
- Docker build 검증 workflow 작성

### 9.5 Test

- Provider API 테스트 작성
- Preset API 테스트 작성
- Request Preview 테스트 작성
- Secret Config 저장/삭제 테스트 작성
- History 저장 테스트 작성

## 10. 역할 분담 예시

실제 팀원 수에 맞춰 조정한다.

### 10.1 3인 팀 예시

| 팀원 | 담당 |
| --- | --- |
| A | Backend, DB, Provider Adapter |
| B | Frontend, UI, API 연동 |
| C | Docker, CI/CD, 문서, 테스트 보조 |

### 10.2 4인 팀 예시

| 팀원 | 담당 |
| --- | --- |
| A | Backend API, Provider Adapter |
| B | Database, History, 테스트 |
| C | Frontend UI, Request Builder |
| D | Docker, GitHub Actions, 문서, 발표 |

### 10.3 5인 팀 예시

| 팀원 | 담당 |
| --- | --- |
| A | Backend API |
| B | Provider Adapter, API 실행 로직 |
| C | Frontend UI |
| D | Database, 테스트 |
| E | Docker, CI/CD, 문서, 발표 |

## 11. 일정 계획 초안

실제 과제 기간에 맞춰 주차를 조정한다.

| 단계 | 주요 목표 | 산출물 |
| --- | --- | --- |
| 1단계 | 기획 확정, 레포 정리, Issues/Projects 생성 | README, 계획서, Issue 목록 |
| 2단계 | Backend/DB 기본 구조 구현 | Flask app, SQLite 모델, 기본 CRUD API |
| 3단계 | Frontend 기본 화면 구현 | React layout, Provider/Preset 화면 |
| 4단계 | 요청 빌더와 실행 기능 구현 | JSON preview, execute API, Adapter |
| 5단계 | 히스토리, 오류 처리, Docker 구성 | History 화면, Docker Compose |
| 6단계 | CI/CD, 테스트, 문서, 발표 준비 | GitHub Actions, 최종 README, 발표 자료 |

## 12. 과제 요구사항 대응표

| 과제 요구사항 | 계획서에 남길 내용 | 실제 산출물 |
| --- | --- | --- |
| GitHub Pull Requests | 기능 단위 PR 운영 | PR 기록 |
| GitHub Issues | 작업을 Issue로 등록하고 관리 | Issue 목록, 라벨 |
| GitHub Projects | 진행 상태를 보드로 관리 | Project board |
| Web Framework | Flask 백엔드 사용 | Flask app |
| REST API development | Provider, Preset, Execute, History API | API 코드, API 문서 |
| Database integration | SQLite에 설정/프리셋/히스토리 저장 | SQLite 모델, DB 파일 |
| Containerization | Docker Compose로 로컬 실행 환경 제공 | Dockerfile, compose file |
| DevOps | GitHub Actions로 테스트/빌드 자동화 | workflow 파일, Actions 기록 |
| Cloud Environment | MVP 제외, Docker 기반 확장 가능 | 계획서의 제외/확장 항목 |
| Monitoring | MVP 제외, 실행 로그 저장으로 대체 | execution_logs 테이블 |
| Code Contribution | 브랜치, 커밋, PR 단위 기여 | Git history |
| Collaboration | 리뷰와 Issue 기반 협업 | PR comments, Issue comments |
| Project Management | Project board 진행 관리 | Backlog/Todo/In Progress/Review/Done |

## 13. 리스크와 대응 전략

| 리스크 | 영향 | 대응 |
| --- | --- | --- |
| 3사 Provider API 차이 | 구현 일정 지연 | Adapter 구조를 만들고 Custom/OpenAI-compatible부터 구현 |
| API Key 노출 | 보안 문제 | 로컬 저장, Git 추적 제외, 마스킹 응답 |
| 기능 범위 과다 | MVP 미완성 | MVP와 확장 기능을 문서에서 명확히 분리 |
| Docker 설정 지연 | 제출 요구사항 미충족 | 초기에 최소 Docker Compose를 먼저 구성 |
| 팀원 역할 중복 | 작업 충돌 | Issue 담당자 지정, PR 단위 리뷰 |
| 프론트/백엔드 연동 지연 | 통합 문제 | API 초안을 먼저 합의하고 mock data로 UI 병행 개발 |

## 14. 최종 제출 산출물 체크리스트

제출 전 다음 항목을 확인한다.

- GitHub Repository가 공개 또는 제출 가능한 상태이다.
- README에 프로젝트 소개와 실행 방법이 있다.
- `docs/PROJECT_PLAN.md`에 프로젝트 계획이 정리되어 있다.
- `docs/TECH_DECISIONS.md`에 기술 선택 이유가 정리되어 있다.
- `docs/COLLABORATION_PLAN.md`에 협업 방식이 정리되어 있다.
- REST API 주요 endpoint가 문서화되어 있다.
- DB 테이블 설계가 문서화되어 있다.
- Docker 실행 방법이 문서화되어 있다.
- GitHub Actions workflow가 존재한다.
- Issues와 PR 기록이 남아 있다.
- GitHub Projects 보드에 진행 상태가 남아 있다.
- 최종 발표 자료 또는 시연 시나리오가 준비되어 있다.

## 15. 내일 회의 아젠다

내일 회의에서는 다음 순서로 결정한다.

1. PromptDeck의 로컬 실행형 도구 컨셉을 팀 확정안으로 둘지 확인한다.
2. Flask, React + Vite, SQLite 선택에 팀원 동의가 있는지 확인한다.
3. MVP에서 실제 요청 실행을 어디까지 할지 결정한다.
4. API Key 저장 방식을 로컬 설정 파일로 확정할지 결정한다.
5. 팀원별 역할을 정한다.
6. 첫 주차 Issue 목록을 만든다.
7. GitHub Projects 보드 컬럼과 운영 규칙을 정한다.
8. 계획서 제출 문서의 최종 담당자를 정한다.
