# PromptDeck 프론트엔드 구현 현황

> 기준 브랜치: `develope`
> 기준일: 2026-06-10
> 목적: 현재 구현된 MVP 화면과 API 연동 상태 정리

---

## 1. 요약

현재 프론트엔드는 React + Vite 기반 MVP 화면이 구현되어 있으며, 백엔드의 인증, Provider Key, Provider 설정, Provider 옵션, 실행 프리셋, 요청 실행, 실행 기록, 조직 API와 연결되어 있다.

프론트엔드는 `/login`, `/signup`을 제외한 주요 화면을 인증 보호 라우트로 감싸고, 공통 API 클라이언트에서 access token 만료 시 refresh token 재발급 흐름을 처리한다. Provider 설정 화면은 백엔드 옵션 API를 우선 사용하고, 응답을 받을 수 없는 경우에만 로컬 대체값을 사용한다.

---

## 2. 화면 구성

| 경로 | 화면 | 상태 |
| --- | --- | --- |
| `/login` | 로그인 | 구현됨 |
| `/signup` | 회원가입 | 구현됨 |
| `/` | 대시보드 | 구현됨 |
| `/providers` | Provider API Key 관리 | 구현됨 |
| `/provider-settings` | Provider 요청 설정 관리 | 구현됨 |
| `/execution` | 요청 실행, 미리보기, 실행 프리셋 관리 | 구현됨 |
| `/history` | 실행 기록 조회 | 구현됨 |
| `/organizations` | 조직 생성, 조회, 멤버 추가 | 구현됨 |

---

## 3. 주요 프론트엔드 모듈

| 파일 | 역할 |
| --- | --- |
| `src/App.jsx` | 전체 라우팅과 인증 보호 라우트 연결 |
| `src/api/client.js` | 공통 fetch 래퍼, 인증 헤더, refresh token 재발급, 에러 처리 |
| `src/api/auth.js` | 회원가입, 로그인, 로그아웃 API |
| `src/api/providers.js` | Provider API Key CRUD API |
| `src/api/providerSettings.js` | Provider 설정 CRUD 및 옵션 API |
| `src/api/executions.js` | 요청 실행, 미리보기, 실행 기록, 실행 프리셋 API |
| `src/api/organizations.js` | 조직 생성, 조회, 멤버 추가 API |
| `src/constants/providerOptions.js` | 옵션 API 장애 시 사용하는 로컬 대체값 |
| `src/hooks/useProviderOptions.js` | Provider 타입별 옵션 로딩과 기본값 적용 |
| `src/utils/providerOptionsResolver.js` | 옵션 API 응답과 로컬 대체값을 화면용 선택지로 변환 |
| `src/components/Navbar.jsx` | 주요 화면 네비게이션 |
| `src/components/PrivateRoute.jsx` | 인증 필요 화면 보호 |
| `src/components/ui/FormField.jsx` | 공통 폼 필드 래퍼 |

---

## 4. 백엔드 API 연동 현황

| 기능 | Method | 경로 | 프론트엔드 사용 |
| --- | --- | --- | --- |
| 회원가입 | POST | `/api/auth/signup` | `auth.js` |
| 로그인 | POST | `/api/auth/login` | `auth.js` |
| 토큰 갱신 | POST | `/api/auth/refresh` | `client.js`, `PrivateRoute.jsx` |
| 로그아웃 | POST | `/api/auth/logout` | `auth.js` |
| Provider API Key | GET/POST/PUT/DELETE | `/api/provider-keys` | `providers.js` |
| Provider 설정 | GET/POST/PUT/DELETE | `/api/provider-settings` | `providerSettings.js` |
| Provider 옵션 | GET | `/api/provider-settings/options` | `useProviderOptions.js` |
| 요청 실행 | POST | `/api/provider-executions` | `executions.js` |
| 요청 미리보기 | POST | `/api/provider-executions/preview` | `executions.js` |
| 실행 기록 | GET | `/api/provider-executions/history` | `executions.js` |
| 실행 프리셋 | GET/POST/PUT/DELETE | `/api/provider-execution-presets` | `executions.js` |
| 조직 | GET/POST | `/api/organizations` | `organizations.js` |
| 조직 멤버 추가 | POST | `/api/organizations/{id}/members` | `organizations.js` |

---

## 5. 현재 아키텍처

```text
frontend/src/
├── api/
│   ├── auth.js
│   ├── client.js
│   ├── executions.js
│   ├── organizations.js
│   ├── providers.js
│   └── providerSettings.js
├── components/
│   ├── Navbar.jsx
│   ├── PrivateRoute.jsx
│   └── ui/
│       └── FormField.jsx
├── constants/
│   └── providerOptions.js
├── hooks/
│   └── useProviderOptions.js
├── pages/
│   ├── DashboardPage.jsx
│   ├── ExecutionPage.jsx
│   ├── HistoryPage.jsx
│   ├── LoginPage.jsx
│   ├── OrganizationsPage.jsx
│   ├── ProviderSettingsPage.jsx
│   ├── ProvidersPage.jsx
│   └── SignupPage.jsx
├── styles/
│   └── app.css
├── utils/
│   └── providerOptionsResolver.js
├── App.jsx
└── main.jsx
```

---

## 6. 검증 및 후속 과제

현재 문서 기준으로 기능 화면과 API 연결은 MVP 범위에 맞게 구현되어 있다. 남은 작업은 기능 추가보다 배포 환경에서의 검증과 UX 안정화에 가깝다.

- GCP 배포 주소에서 회원가입, 로그인, Provider 설정, 요청 미리보기, 요청 실행, 기록 조회 흐름 확인
- 실제 Provider API Key를 사용한 외부 API 실행 검증
- 전역 에러 바운더리와 실패 상태 UI 보강
- 모바일 폭에서 폼과 테이블 레이아웃 점검
- 조직 기능의 권한 정책과 팀 단위 공유 범위는 별도 이슈에서 확장 검토
