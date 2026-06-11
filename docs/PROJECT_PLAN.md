# PromptDeck 프로젝트 계획서

> 상태: 팀 회의 후 확정안
> 목적: 과제 제출 및 구현 기준 문서
> 비고: 로그인 구현 방식은 담당 구현 과정에서 확정한다.

## 관련 문서

- [프로젝트 제안서](PROJECT_PROPOSAL.md)
- [협업 운영 계획](COLLABORATION_PLAN.md)
- [시스템 아키텍처 다이어그램](system_architecture.drawio)

## 1. 프로젝트 개요

PromptDeck은 GCP 서버에 배포되는 LLM API 요청 빌더 웹 서비스이다.

사용자는 로그인 후 OpenAI, Gemini, Claude, Custom API 등 여러 LLM Provider의 요청 형식을 설정하고, 자주 사용하는 실행 프리셋을 저장한 뒤, 선택한 Provider로 요청을 실행할 수 있다. 실행 결과와 요청 기록은 MySQL에 저장되며, 사용자별로 Provider 설정, 실행 프리셋, API Key 기록, 실행 히스토리가 분리된다.

## 2. 프로젝트 배경

LLM API는 Provider마다 endpoint, header, request body, response format이 다르다. 여러 Provider를 테스트하거나 프롬프트를 반복적으로 실험하려면 매번 JSON 요청을 직접 작성해야 하며, 자주 사용하는 프롬프트를 체계적으로 관리하기도 어렵다.

PromptDeck은 이러한 문제를 줄이기 위해 다음 기능을 제공한다.

- Provider별 요청 형식 관리
- 실행 프리셋 저장 및 재사용
- API 요청 JSON 미리보기
- 실제 LLM API 요청 실행
- 요청/응답 기록 저장
- 로그인 기반 사용자별 데이터 분리
- Custom API 확장을 통한 유연한 테스트 환경 제공

## 3. 프로젝트 목표

### 3.1 기능적 목표

- 사용자가 회원가입 또는 로그인 후 서비스를 이용할 수 있다.
- 사용자가 자신의 LLM Provider 설정을 등록, 조회, 수정, 삭제할 수 있다.
- 사용자가 자신의 실행 프리셋을 등록, 조회, 수정, 삭제할 수 있다.
- 사용자가 선택한 Provider 설정과 실행 프리셋을 조합해 API 요청을 생성할 수 있다.
- 생성된 요청 JSON을 실행 전에 확인할 수 있다.
- 사용자가 Provider API Key를 보안 저장하고, 저장 상태 확인과 삭제를 할 수 있다.
- 요청 결과와 오류 정보를 사용자별 기록으로 저장할 수 있다.
- Custom API Provider를 통해 임의의 REST API 요청 형식을 등록할 수 있다.

### 3.2 비기능적 목표

- 과제 기간 안에 구현 가능한 범위로 MVP를 완성한다.
- REST API, DB, Docker, CI/CD, Cloud Environment 등 과제 기술 요구사항을 명확히 충족한다.
- 팀원이 역할을 나누기 쉬운 구조로 개발한다.
- 이후 Provider를 확장할 수 있도록 Adapter 구조를 둔다.
- Provider API Key가 평문으로 저장되거나 응답/로그에 노출되지 않도록 관리한다.
- GCP 배포 환경에서 실행 가능한 구조를 갖춘다.

## 4. MVP 범위

MVP에서 구현할 기능은 다음과 같다.

- 회원가입/로그인 기능
- 사용자별 Provider 설정 관리
- 사용자별 실행 프리셋 관리
- 요청 JSON 빌더
- Provider API Key 보안 저장, 마스킹 표시, 삭제 기능
- LLM 요청 실행
- 요청/응답 히스토리 저장
- 요청 실행 테스트 화면
- MySQL 기반 데이터 저장
- GCP 서버 배포
- Docker 기반 실행 환경
- GitHub Actions 기반 테스트/빌드 확인

MVP에서 제외할 기능은 다음과 같다.

- 팀 단위 프리셋 공유
- Provider별 사용 요금 계산
- 실시간 협업 편집
- 고급 모니터링 대시보드
- 세부 권한 관리 기능

## 5. 주요 사용자 시나리오

### 5.1 로그인

사용자는 계정을 생성하거나 로그인한 뒤 자신의 Provider 설정, 실행 프리셋, 실행 기록에 접근한다. 로그인 구현 방식은 담당 구현 과정에서 정한다.

### 5.2 Provider 설정

사용자는 OpenAI, Gemini, Claude, Custom API 중 하나를 선택하고, endpoint, model, header, body template 등 요청에 필요한 정보를 설정한다.

### 5.3 실행 프리셋 생성

사용자는 번역, 요약, 코드 리뷰, 글쓰기 보조 등 반복적으로 사용하는 요청 입력값과 실행 옵션을 프리셋으로 저장한다.

### 5.4 요청 생성 및 실행

사용자는 Provider 설정과 실행 프리셋을 선택하고 입력값을 작성한다. PromptDeck은 선택된 Provider의 요청 형식에 맞는 JSON을 생성하고, 사용자가 확인한 뒤 요청을 실행한다.

### 5.5 요청 기록 확인

사용자는 과거 요청의 Provider 설정, 실행 프리셋, 요청 본문, 응답 본문, 실행 성공 여부, 오류 메시지 등을 확인한다.

## 6. 시스템 구성

확정 구성은 다음과 같다.

```text
GCP
  Frontend
    React + Vite
    - 로그인 화면
    - Provider 설정 화면
    - 실행 프리셋 관리 화면
    - 요청 JSON 미리보기
    - 요청 실행 화면
    - 요청 기록 화면

  Backend
    Java Spring REST API Server
    - Auth API
    - Provider API Key API
    - Provider Setting API
    - Provider Options API
    - Execution Preset API
    - Request Preview API
    - Provider Execution API
    - History API
    - Provider Adapter Layer

  Database
    MySQL
    - users
    - refresh_tokens
    - organizations
    - organization_members
    - provider_api_keys
    - provider_settings
    - provider_execution_presets
    - provider_execution_histories

External LLM Providers
  - OpenAI API
  - Gemini API
  - Claude API
  - Custom REST API
```

## 7. 과제 요구사항 대응

| 요구사항 | PromptDeck 대응 방식 |
| --- | --- |
| GitHub Pull Requests | 기능 단위 브랜치와 PR 생성 |
| GitHub Issues | 기능, 버그, 문서 작업을 Issue로 관리 |
| GitHub Projects | Backlog, Todo, In Progress, Review, Done으로 진행 상황 관리 |
| Web Framework | Java Spring 기반 백엔드 구현 |
| REST API | Auth, Provider Key, Provider Setting, Provider Options, Execution Preset, History, Execution API 제공 |
| Database Integration | MySQL 기반 사용자, refresh token, 조직, Provider 설정, 실행 프리셋, 히스토리 데이터 저장 |
| Containerization | Dockerfile 및 Docker Compose 구성 |
| DevOps | GitHub Actions로 테스트/빌드 자동화 |
| Cloud Environment | GCP 서버 배포 |
| Monitoring | MVP에서는 실행 로그와 오류 기록 중심으로 관리 |

## 8. 데이터 저장 정책

- 사용자 계정, refresh token, 조직, Provider 설정, 실행 프리셋, 요청 기록은 MySQL에 저장한다.
- 사용자별 데이터는 로그인 사용자 기준으로 분리한다.
- Provider API Key는 평문 저장을 금지하고, UI/API 응답에는 저장 여부와 마스킹된 값만 표시한다.
- 실제 Provider 요청 실행에 필요한 Key 처리 방식은 백엔드 구현에서 보안 저장 정책에 맞춰 적용한다.
- 요청/응답 로그에는 원본 API Key가 남지 않도록 한다.

## 9. 예상 일정

| 단계 | 주요 작업 |
| --- | --- |
| 1단계 | 프로젝트 구조 생성, README/docs 작성, 협업 규칙 정리 |
| 2단계 | Java Spring REST API 기본 구조 및 MySQL 스키마 구현 |
| 3단계 | React 화면 구조 및 로그인/Provider/Preset 관리 UI 구현 |
| 4단계 | 요청 JSON 빌더 및 LLM 요청 실행 기능 구현 |
| 5단계 | 요청/응답 히스토리, 오류 처리, Docker 및 GCP 배포 구성 |
| 6단계 | GitHub Actions, 테스트, 문서 정리, 발표 준비 |

## 10. 주요 리스크와 대응

| 리스크 | 대응 |
| --- | --- |
| Provider별 API 형식 차이가 큼 | Adapter 구조로 Provider별 변환 로직 분리 |
| Provider API Key 노출 위험 | 평문 저장 금지, 마스킹 표시, 로그 기록 제한 |
| 로그인 구현 방식 충돌 | 문서에서는 기능 요구와 사용자별 데이터 분리만 명시 |
| 기능 범위 과다 | MVP와 확장 기능을 명확히 분리 |
| 팀원 간 역할 중복 | Issues와 Projects로 작업 단위 분리 |
| GCP/Docker/CI 설정 지연 | 초기부터 최소 배포 구성을 준비하고 점진 개선 |

## 11. 향후 확장 가능성

- Provider 추가: Ollama, OpenRouter, LM Studio 등
- 프리셋 버전 관리
- 프리셋 import/export
- 요청 비용 및 토큰 사용량 추정
- 팀 단위 프리셋 공유
- 고급 권한 관리
- Grafana 등 모니터링 도구 연동
