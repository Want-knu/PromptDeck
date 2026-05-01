# PromptDeck 협업 운영 계획

> 상태: 팀 회의 후 확정안
> 목적: GitHub Issues, Pull Requests, Projects를 활용한 과제 협업 방식 정리

## 1. 협업 목표

PromptDeck은 과제 평가 기준에 맞춰 GitHub 기반 협업 과정을 명확히 남긴다. 기능 구현뿐 아니라 Issue 관리, Pull Request 기록, Project 진행 상황을 함께 관리하여 팀원의 기여 과정이 보이도록 한다.

확정된 구현 방향은 React + Vite 프론트엔드, Java Spring 백엔드, MySQL 데이터베이스, GCP 배포, GitHub Actions CI/CD이다.

## 2. GitHub Issues 운영

모든 작업은 가능한 한 Issue로 먼저 등록한다.

Issue 유형:

- Feature: 새로운 기능 구현
- Bug: 오류 수정
- Docs: 문서 작성 및 수정
- Chore: 설정, Docker, CI, 배포 환경 작업
- Refactor: 동작 변경 없는 구조 개선
- Test: 테스트 코드 작성 및 보강

Issue 예시:

- 로그인 화면 및 사용자별 접근 흐름 구현
- Provider 설정 CRUD API 구현
- 프롬프트 프리셋 CRUD API 구현
- 요청 JSON 빌더 화면 구현
- Custom API 요청 실행 기능 구현
- MySQL 스키마 및 마이그레이션 구성
- Provider API Key 보안 저장 및 마스킹 처리
- Docker Compose 실행 환경 구성
- GCP 배포 설정 정리
- GitHub Actions CI 구성
- README 및 프로젝트 계획서 작성

## 3. GitHub Projects 운영

Project 보드는 다음 컬럼으로 관리한다.

| 컬럼 | 의미 |
| --- | --- |
| Backlog | 아직 착수하지 않은 아이디어 또는 후보 작업 |
| Todo | 이번 단계에서 진행하기로 한 작업 |
| In Progress | 현재 작업 중인 Issue |
| Review | PR 생성 후 리뷰 대기 중인 작업 |
| Done | 완료 및 병합된 작업 |

운영 원칙:

- Issue를 생성하면 Project에 연결한다.
- 작업 시작 시 `In Progress`로 이동한다.
- PR을 생성하면 `Review`로 이동한다.
- PR이 merge되면 `Done`으로 이동한다.

## 4. 브랜치 전략

기본 브랜치:

- `main`: 제출 가능한 안정 버전
- `develop`: 기능 통합 브랜치

작업 브랜치 예시:

- `feature/auth-flow`
- `feature/provider-crud`
- `feature/preset-crud`
- `feature/request-builder`
- `feature/chat-execution`
- `feature/history-view`
- `feature/api-key-security`
- `chore/mysql-schema`
- `chore/docker-compose`
- `chore/gcp-deploy`
- `ci/github-actions`
- `docs/project-plan`

브랜치 이름 규칙:

```text
type/short-description
```

type 예시:

- `feature`
- `fix`
- `docs`
- `chore`
- `refactor`
- `test`
- `ci`

## 5. Pull Request 운영

PR은 기능 단위로 작게 만든다.

PR 작성 시 포함할 내용:

- 연결된 Issue 번호
- 구현 내용 요약
- 테스트 방법
- 스크린샷 또는 실행 결과
- 리뷰어가 확인해야 할 부분

PR 제목 예시:

```text
feat: add prompt preset CRUD API
feat: add provider adapter layer
docs: update final project plan
ci: add GitHub Actions workflow
```

PR 본문 예시:

```md
## Summary
- 프롬프트 프리셋 생성/조회/수정/삭제 API를 추가했습니다.
- MySQL 테이블과 service layer를 추가했습니다.

## Test
- [ ] Backend unit tests pass
- [ ] Manual API test with sample preset

Closes #3
```

## 6. Commit 메시지 규칙

권장 형식:

```text
type: short summary
```

type 예시:

- `feat`: 기능 추가
- `fix`: 버그 수정
- `docs`: 문서 변경
- `style`: 포맷 변경
- `refactor`: 리팩터링
- `test`: 테스트 추가/수정
- `chore`: 설정 및 기타 작업
- `ci`: CI/CD 설정

예시:

```text
feat: add provider settings model
docs: update project plan
ci: run backend tests on pull request
```

## 7. 역할 분담 예시

역할은 팀 상황에 맞게 조정한다.

| 역할 | 담당 작업 |
| --- | --- |
| Frontend | React 화면, 상태 관리, API 연동 |
| Backend | Java Spring REST API, Provider Adapter, 실행 로직 |
| Database | MySQL 스키마, 저장 구조, 히스토리 관리 |
| DevOps | Docker, GitHub Actions, GCP 배포 가이드 |
| Documentation | README, 계획서, API 문서, 발표 자료 |

## 8. Definition of Done

작업 완료 기준:

- Issue 요구사항을 충족한다.
- 관련 코드가 PR로 제출된다.
- 필요한 테스트 또는 수동 확인이 완료된다.
- README 또는 docs 업데이트가 필요한 경우 반영한다.
- 리뷰 후 `main` 또는 `develop`에 merge된다.
- Project 상태가 `Done`으로 이동된다.
