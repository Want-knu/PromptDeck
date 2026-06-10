# PromptDeck 프론트엔드 구현 현황

> 작성일: 2026-05-17
> 작성자: 프론트엔드 담당
> 마지막 작업: 이슈 #24 (프론트엔드 아키텍처 재설계) 진행 중

---

## 1. 현재 프론트엔드 코드 현황

### 1.1 develope 브랜치 기준 (PR #30 병합 상태)

현재 `develope`에 병합된 프론트엔드는 **MVP 기능 구현 완료** 상태입니다.

| 파일 | 내용 |
|---|---|
| `App.jsx` | 라우팅 (Login, Signup, Dashboard, Providers, ProviderSettings, Execution, History, Organizations) |
| `api/auth.js` | 로그인/회원가입/로그아웃 API |
| `api/client.js` | 공통 fetch 래퍼 (authHeaders, 토큰 갱신, 에러 핸들링) |
| `api/providers.js` | Provider Key CRUD API |
| `api/providerSettings.js` | Provider 설정 CRUD API |
| `api/executions.js` | 실행/미리보기/기록조회 API |
| `api/organizations.js` | 조직 생성/조회/멤버추가 API |
| `constants/providerOptions.js` | Provider별 mock 모델/프리셋 데이터 |
| `components/Navbar.jsx` | 네비게이션 바 (전체 링크 포함) |
| `components/PrivateRoute.jsx` | 인증 보호 라우트 (refresh token 지원) |
| `components/ui/FormField.jsx` | **[NEW]** 재사용 폼 필드 래퍼 |
| `hooks/useProviderOptions.js` | **[NEW]** providerType 변경 시 필드 초기화 훅 |
| `pages/LoginPage.jsx` | 로그인 페이지 |
| `pages/SignupPage.jsx` | 회원가입 페이지 |
| `pages/DashboardPage.jsx` | 대시보드 (4개 기능 카드) |
| `pages/ProvidersPage.jsx` | Provider Key 관리 (CRUD) |
| `pages/ProviderSettingsPage.jsx` | Provider 설정 관리 (FormField, useProviderOptions 적용) |
| `pages/ExecutionPage.jsx` | 요청 실행 페이지 (미리보기/실행) |
| `pages/HistoryPage.jsx` | 실행 기록 조회 페이지 |
| `pages/OrganizationsPage.jsx` | 조직 관리 페이지 |

---

## 2. PR #23 리뷰 피드백 — 전체 해결 완료

| # | 항목 | 심각도 | 상태 |
|---|---|---|---|
| 1 | 모델명 select 변경 | 🔴 High | ✅ 해결 — providerType별 select 사용 |
| 2 | providerType 변경 시 필드 초기화 | 🔴 High | ✅ 해결 — useProviderOptions 훅으로 분리 |
| 3 | endpoint 프리셋 자동완성 | 🟡 Medium | ✅ 해결 — CUSTOM 아닐 때 select로 표시 |
| 4 | bodyTemplateJson 자유 입력 UX | 🟡 Medium | ✅ 해결 — CUSTOM 아닐 때 disabled 처리 |
| 5 | authHeaders 중복 제거 | 🔴 High | ✅ 해결 — client.js로 통합 |
| 6 | mock/options layer 분리 | 🟡 Medium | ✅ 해결 — constants/providerOptions.js |제한 | 🟡 Medium | textarea로 아무 JSON이나 입력 가능 → provider별 기본 템플릿 기반으로 제한 필요 |
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

---

## 3. 현재 아키텍처

```
frontend/src/
├── api/
│   ├── client.js          ← 공통 fetch 래퍼 (authHeaders, 토큰 갱신, 에러 핸들링)
│   ├── auth.js
│   ├── providers.js
│   ├── providerSettings.js
│   ├── executions.js
│   └── organizations.js
├── constants/
│   └── providerOptions.js ← mock 데이터 (추후 API로 교체)
├── hooks/
│   └── useProviderOptions.js ← providerType 변경 시 필드 초기화 훅
├── components/
│   ├── ui/
│   │   └── FormField.jsx  ← 재사용 폼 필드 래퍼
│   ├── Navbar.jsx
│   └── PrivateRoute.jsx
├── pages/
│   ├── DashboardPage.jsx
│   ├── ProvidersPage.jsx
│   ├── ProviderSettingsPage.jsx
│   ├── ExecutionPage.jsx
│   ├── HistoryPage.jsx
│   ├── OrganizationsPage.jsx
│   ├── LoginPage.jsx
│   └── SignupPage.jsx
├── App.jsx
└── main.jsx
```

---

## 4. 향후 과제

1. **Options API 연동** — 백엔드에 `/api/provider-settings/options` API 구축 후 mock → API 교체
2. **UI 컴포넌트 확장** — `components/ui/`에 Select, Button, Modal 등 추가
3. **에러 바운더리** — 전역 에러 처리 강화
4. **반응형 디자인** — 모바일 대응