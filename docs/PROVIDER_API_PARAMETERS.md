# Provider API 조정 가능 파라미터 정리

> 작성일: 2026-05-13
> 정리 기준: 현재 백엔드 Provider Definition과 옵션 API 설계
> 목적: PromptDeck Provider 설정 화면에서 사용자가 조정할 수 있는 파라미터 범위 정리

---

## 1. 문서 성격

이 문서는 Provider별 API 명세의 전체 복제본이 아니라 PromptDeck 구현에서 사용하는 옵션 설계를 설명하는 참고 문서이다. Provider 모델명과 세부 파라미터는 수시로 바뀔 수 있으므로, 실제 운영 적용 전에는 각 Provider의 공식 문서를 다시 확인해야 한다.

현재 구현은 백엔드의 Provider Definition을 기준으로 `/api/provider-settings/options` 응답을 생성하고, 프론트엔드는 이 응답을 우선 사용한다. 프론트엔드는 옵션 API 응답을 받을 수 없는 경우에만 로컬 대체값으로 화면 선택지를 구성한다.

---

## 2. 공통 설계

| 항목 | 현재 구현 |
| --- | --- |
| Provider 타입 | `OPENAI`, `GEMINI`, `CLAUDE`, `CUSTOM` |
| HTTP Method | `POST`, `GET`, `PUT`, `PATCH`, `DELETE` |
| 인증 방식 | `BEARER`, `HEADER`, `QUERY_PARAM`, `NONE` |
| 옵션 공급 경로 | `GET /api/provider-settings/options` |
| 설정 저장 경로 | `/api/provider-settings` |
| 미리보기 경로 | `POST /api/provider-executions/preview` |
| 실행 경로 | `POST /api/provider-executions` |
| 실행 기록 경로 | `GET /api/provider-executions/history` |

Provider 설정은 endpoint, method, auth type, 인증 헤더명 또는 쿼리 파라미터명, 추가 헤더, 추가 쿼리 파라미터, 요청 본문 템플릿, 응답 추출 경로, 모델별 옵션 스키마를 함께 관리한다.

---

## 3. OpenAI

현재 백엔드 구현은 OpenAI Responses API를 기본값으로 사용한다.

| 항목 | 값 |
| --- | --- |
| 기본 endpoint | `https://api.openai.com/v1/responses` |
| 인증 방식 | `BEARER` |
| 인증 헤더 | `Authorization` |
| 기본 응답 추출 경로 | `output_text` |
| 옵션 그룹 | `GPT options` |

### 참고 모델

현재 Provider Definition에는 다음 모델 ID가 등록되어 있다.

- `gpt-5.5`
- `gpt-5.4`
- `gpt-5.4-mini`
- `gpt-4o`
- `gpt-4o-mini`

### 조정 가능 옵션

| 옵션 | 타입 | 범위/선택지 |
| --- | --- | --- |
| `temperature` | number | `0` - `2` |
| `max_output_tokens` | integer | 최소 `16` |
| `presence_penalty` | number | `-2` - `2` |
| `frequency_penalty` | number | `-2` - `2` |
| `reasoning.effort` | string | `none`, `minimal`, `low`, `medium`, `high`, `xhigh` |
| `reasoning.summary` | string | `auto`, `concise`, `detailed` |
| `top_p` | number | `0` - `1`, 일부 모델 옵션 |
| `stop` | array | 문자열 배열, 일부 모델 옵션 |

---

## 4. Gemini

현재 백엔드 구현은 Google Generative Language API의 `generateContent` endpoint 템플릿을 사용한다.

| 항목 | 값 |
| --- | --- |
| endpoint 템플릿 | `https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent` |
| 인증 방식 | `QUERY_PARAM` |
| 인증 쿼리 파라미터 | `key` |
| 기본 응답 추출 경로 | `candidates[0].content.parts[0].text` |
| 옵션 그룹 | `Gemini generation options` |

### 참고 모델

현재 Provider Definition에는 다음 모델 ID가 등록되어 있다.

- `gemini-3.5-flash`
- `gemini-3.1-pro-preview`
- `gemini-3-flash-preview`
- `gemini-2.5-pro`
- `gemini-2.5-flash`
- `gemini-2.5-flash-lite`

### 조정 가능 옵션

| 옵션 | 타입 | 범위/선택지 |
| --- | --- | --- |
| `temperature` | number | `0` - `2` |
| `generationConfig.topP` | number | `0` - `1` |
| `generationConfig.topK` | integer | 모델별 값 |
| `generationConfig.maxOutputTokens` | integer | 모델별 값 |
| `generationConfig.responseMimeType` | string | `text/plain`, `application/json` |
| `thinkingConfig.thinkingLevel` | string | `MINIMAL`, `LOW`, `MEDIUM`, `HIGH` |
| `thinkingConfig.includeThoughts` | boolean | true/false |
| `thinkingConfig.thinkingBudget` | integer | 모델별 값 |

Gemini 3 계열은 `thinkingConfig.thinkingLevel` 중심의 옵션 스키마를 사용하고, Gemini 2.5 계열은 `thinkingConfig.thinkingBudget` 중심의 옵션 스키마를 사용한다.

---

## 5. Claude

현재 백엔드 구현은 Anthropic Claude Messages API를 기본값으로 사용한다.

| 항목 | 값 |
| --- | --- |
| 기본 endpoint | Claude Messages API endpoint |
| 인증 방식 | `HEADER` |
| 인증 헤더 | `x-api-key` |
| 기본 추가 헤더 | `anthropic-version: 2023-06-01` |
| 기본 응답 추출 경로 | `content[0].text` |
| 옵션 그룹 | `Claude thinking options` |

### 참고 모델

현재 Provider Definition에는 다음 모델 ID가 등록되어 있다.

- `claude-opus-4-7`
- `claude-opus-4-6`
- `claude-sonnet-4-6`
- `claude-haiku-4-5-20251001`

### 조정 가능 옵션

| 옵션 | 타입 | 범위/선택지 |
| --- | --- | --- |
| `max_tokens` | integer | 최소 `1` |
| `thinking.display` | string | `summarized`, `omitted` |
| `output_config.effort` | string | `low`, `medium`, `high` |
| `stop_sequences` | array | 문자열 배열 |
| `system` | string | 시스템 프롬프트 |
| `thinking.type` | string | `adaptive`, `disabled`, 일부 모델은 `enabled` |
| `thinking.budget_tokens` | integer | 일부 모델 옵션 |
| `temperature` | number | `0` - `1`, 일부 모델 옵션 |

Claude Provider Definition은 필수 버전 헤더를 기본 추가 헤더로 포함한다.

---

## 6. Custom API

Custom Provider는 고정된 공식 모델 목록을 갖지 않고 사용자가 endpoint와 요청 형식을 직접 정의한다.

| 항목 | 값 |
| --- | --- |
| endpoint | 사용자 입력 |
| method | 사용자 선택 |
| 인증 방식 | 사용자 선택 |
| 인증 헤더명 | 필요 시 사용자 입력 |
| 인증 쿼리 파라미터명 | 필요 시 사용자 입력 |
| 요청 본문 템플릿 | 사용자 입력 |
| 응답 추출 경로 | 사용자 입력 |

Custom API는 외부 REST API를 테스트하기 위한 확장 지점이다. 사용자 입력값 검증과 실패 응답 처리는 백엔드 실행 흐름에서 담당한다.

---

## 7. 구현 반영 방식

Provider 옵션은 백엔드 Provider Definition에서 관리하고, 프론트엔드는 옵션 API 응답을 화면 선택지와 기본값으로 변환한다.

구현 파일 기준:

- `OpenAiProviderDefinition.java`: OpenAI endpoint, 인증 방식, 모델별 옵션 스키마
- `GeminiProviderDefinition.java`: Gemini endpoint 템플릿, 모델별 thinking 옵션 스키마
- `ClaudeProviderDefinition.java`: Claude endpoint, 버전 헤더, 모델별 thinking 옵션 스키마
- `CustomProviderDefinition.java`: Custom Provider 기본 선택지
- `ProviderSettingOptionsResponse.java`: 옵션 API 응답 구조
- `useProviderOptions.js`: 프론트엔드 옵션 로딩 및 기본값 적용
- `providerOptionsResolver.js`: 옵션 API 응답과 로컬 대체값 정규화
