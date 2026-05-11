# PromptDeck 프론트엔드 구현 현황

> 작성일: 2026-05-11  
> 작성자: 프론트엔드 담당

---

## 1. 현재 프론트엔드 코드 현황

### 1.1 develope 브랜치 기준 (PR #20 병합 상태)

현재 `develope`에 병합된 프론트엔드는 **초기 셋업** 상태입니다.

| 파일 | 내용 |
|---|---|
| `App.jsx` | 라우팅 (Login, Signup, Dashboard, Providers) |
| `api/auth.js` | 로그인/회원가입 API |
| `api/providers.js` | Provider Key CRUD API |
| `components/Navbar.jsx` | 네비게이션 바 |
| `components/PrivateRoute.jsx` | 인증 보호 라우트 |
| `pages/LoginPage.jsx` | 로그인 페이지 |
| `pages/SignupPage.jsx` | 회원가입 페이지 |
| `pages/DashboardPage.jsx` | 대시보드 (4개 기능 카드, 3개 "준비 중") |
| `pages/ProvidersPage.jsx` | Provider Key 관리 (CRUD) |

### 1.2 PR #23 (feature/frontend-v2) — 리뷰 중

PR #23에서 추가된 프론트엔드 기능:

| 파일 | 내용 |
|---|---|
| `api/executions.js` | 실행/미리보기/기록조회 API |
| `api/organizations.js` | 조직 생성/조회/멤버추가 API |
| `api/providerSettings.js` | Provider 설정 CRUD API |
| `constants/providerOptions.js` | **[NEW]** Provider별 mock 모델/프리셋 데이터 |
| `pages/ProviderSettingsPage.jsx` | Provider 설정 관리 페이지 |
| `pages/ExecutionPage.jsx` | 요청 실행 페이지 (미리보기/실행) |
| `pages/HistoryPage.jsx` | 실행 기록 조회 페이지 |
| `App.jsx` (수정) | 라우트 추가 (provider-settings, execution, history) |
| `DashboardPage.jsx` (수정) | "준비 중" → 실제 링크로 변경 |
| `docker-compose.yml` (수정) | PROVIDER_API_KEY 환경변수 추가 |

---

## 2. PR #23 리뷰 피드백

### 2.1 반영된 사항

| 항목 | 상태 | 내용 |
|---|---|---|
| mock options 분리 | ✅ 반영됨 | `constants/providerOptions.js`에 Provider별 모델/프리셋 분리 |
| Provider별 모델 목록 | ✅ 반영됨 | OPENAI/GEMINI/CLAUDE별 모델 select 목록 구성 |
| Provider별 프리셋 | ✅ 반영됨 | endpoint, method, authType, bodyTemplateJson 등 프리셋 정의 |

### 2.2 미반영 사항

| # | 항목 | 심각도 | 내용 |
|---|---|---|---|
| 1 | 모델명 select 변경 | 🔴 High | ProviderSettingsPage에서 모델명이 여전히 input일 가능성. providerType에 따라 select로 변경 필요 |
| 2 | providerType 변경 시 필드 초기화 | 🔴 High | providerType 변경 시 model/endpoint/method/authType/bodyTemplateJson이 이전 provider 기준으로 남는 버그. 별도 handler로 관련 필드 초기화 필요 |
| 3 | endpoint 프리셋 자동완성 | 🟡 Medium | endpoint를 자유 입력 대신 providerType별 프리셋에서 선택 또는 자동 세팅하는 방식 필요 |
| 4 | bodyTemplateJson 자유 입력 제한 | 🟡 Medium | textarea로 아무 JSON이나 입력 가능 → provider별 기본 템플릿 기반으로 제한 필요 |
| 5 | API 모듈 authHeaders 중복 | 🟡 Medium | `authHeaders()` 함수가 executions.js, organizations.js, providerSettings.js에 각각 중복 정의됨 → 공통 모듈 분리 필요 |
| 6 | 컴포넌트 구조 개선 | 🟡 Medium | "전체적인 코드 구조가 변경에 대응하기 힘들다" — 재사용성/확장성 개선 필요 |

---

## 3. 백엔드 API 현황 (PR #22 기준)

프론트엔드와 연동되는 백엔드 API:

| API | Method | 경로 | 비고 |
|---|---|---|---|
| Provider Key CRUD | GET/POST/DELETE | `/api/provider-keys` | 이미 구현됨 |
| Provider Setting CRUD | GET/POST/PUT/DELETE | `/api/provider-settings` | PR #22에서 구현 |
| 실행 | POST | `/api/provider-executions` | PR #22에서 구현 |
| 미리보기 | POST | `/api/provider-executions/preview` | PR #22에서 구현 |
| 실행 기록 | GET | `/api/provider-executions/history` | PR #22에서 구현 |
| 조직 CRUD | GET/POST | `/api/organizations` | PR #22에서 구현 |
| 조직 멤버 추가 | POST | `/api/organizations/{id}/members` | PR #22에서 구현 |
| **Options API** | — | — | **미구현** — Provider별 지원 모델/옵션 조회 API 없음 |

---

## 4. 프론트엔드 재작성 방향 (feature/frontend-v3)

### 4.1 기본 방침

- PR #23 코드를 기반으로 하지 않고, `feature/frontend-v3`에서 새로 작성
- polring의 리뷰 피드백을 **처음부터 반영**하여 설계
- API 연동 로직은 PR #23을 참고하되, 구조는 재설계

### 4.2 작업 분할 계획 (기능별 Issue/PR)

| 순서 | Issue | 내용 | 우선순위 |
|---|---|---|---|
| 1 | 아키텍처 재설계 | API 공통 모듈, mock/options layer, 컴포넌트 구조, 라우팅 | 🔴 최우선 |
| 2 | Provider 설정 페이지 | select + mock options, providerType 변경 시 필드 초기화 | 🔴 High |
| 3 | 실행 페이지 | 미리보기/실행, 결과 표시 | 🟡 Medium |
| 4 | 기록 페이지 | 실행 기록 조회/필터링 | 🟡 Medium |

### 4.3 아키텍처 개선 포인트

```
frontend/src/
├── api/
│   ├── client.js          ← 공통 fetch 래퍼 (authHeaders, 에러 핸들링)
│   ├── auth.js
│   ├── providers.js
│   ├── providerSettings.js
│   ├── executions.js
│   └── organizations.js
├── constants/
│   └── providerOptions.js ← mock 데이터 (추후 API로 교체)
├── hooks/                  ← 커스텀 훅 (useProviderOptions 등)
├── components/
│   ├── ui/                 ← 재사용 UI (Select, Input, Button 등)
│   ├── Navbar.jsx
│   └── PrivateRoute.jsx
├── pages/
│   ├── DashboardPage.jsx
│   ├── ProvidersPage.jsx
│   ├── ProviderSettingsPage.jsx
│   ├── ExecutionPage.jsx
│   └── HistoryPage.jsx
├── App.jsx
└── main.jsx
```

**핵심 변경:**
1. `api/client.js` — authHeaders 중복 제거, 통일된 에러 핸들링
2. `constants/providerOptions.js` — mock 데이터를 별도 layer로 분리 (API 교체 용이)
3. `hooks/` — providerType 변경 시 관련 필드 초기화 로직 등 커스텀 훅으로 분리
4. `components/ui/` — 재사용 가능한 UI 컴포넌트 (Select, FormField 등)

---

## 5. 결정이 필요한 사항

1. **PR #23 처리 방식** — 병합 후 v3에서 리팩터링 vs PR #23을 닫고 v3로 대체
2. **역할 분담** — v3에서 기존에 작업된 부분을 어떻게 이어받을지
3. **Options API 우선순위** — 백엔드에 Options API를 요청할지, mock으로 계속 갈지