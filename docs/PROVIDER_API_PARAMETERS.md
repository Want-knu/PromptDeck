# Provider API 조정 가능 파라미터 정리

> 작성일: 2026-05-13  
> 출처: 각 Provider 공식 API 문서  
> 목적: PromptDeck Provider 설정 페이지에서 사용자가 조정할 수 있는 파라미터 식별

---

## 1. OpenAI (Chat Completions API)

**엔드포인트:** `POST https://api.openai.com/v1/chat/completions`

### 1.1 조정 가능 파라미터

| 파라미터 | 타입 | 범위/기본값 | 설명 |
|---|---|---|---|
| `model` | string | — (필수) | 사용할 모델 ID (예: `gpt-4o`, `gpt-4o-mini`, `gpt-4-turbo`, `gpt-3.5-turbo`) |
| `temperature` | number | 0.0 ~ 2.0, 기본값 1.0 | 출력의 무작위성 제어. 낮을수록 결정적, 높을수록 창의적 |
| `top_p` | number | 0.0 ~ 1.0, 기본값 1.0 | 핵 샘플링(nucleus sampling). temperature와 함께 사용 시 temperature 또는 top_p 중 하나만 권장 |
| `max_tokens` | integer | 1 ~ 모델 한계 | 생성할 최대 토큰 수 |
| `max_completion_tokens` | integer | 1 ~ 모델 한계 | 출력+추론 토큰 포함 최대 토큰 수 (o1/o3 등 reasoning 모델에서 사용) |
| `stop` | string/array | 최대 4개 시퀀스 | 생성을 중지할 문자열 시퀀스 |
| `presence_penalty` | number | -2.0 ~ 2.0, 기본값 0 | 이미 등장한 토큰의 등장 여부에 따른 페널티. 어휘 다양성 증가 |
| `frequency_penalty` | number | -2.0 ~ 2.0, 기본값 0 | 이미 등장한 토큰의 빈도에 비례하는 페널티. 반복 감소 |
| `response_format` | object | — | 출력 형식 지정. `{ "type": "json_object" }` 또는 `{ "type": "json_schema", "json_schema": {...} }` |
| `seed` | integer | — | 결과 재현성을 위한 시드값. 동일한 seed + 동일한 파라미터 → 대부분 동일한 결과 |
| `logprobs` | boolean | 기본값 false | 출력 토큰의 로그 확률 반환 여부 |
| `top_logprobs` | integer | 0 ~ 20 | logprobs=true 시 각 토큰별 반환할 상위 로그 확률 수 |

### 1.2 인증 방식

```
Authorization: Bearer {API_KEY}
```

### 1.3 기본 요청 바디 템플릿

```json
{
  "model": "{{model}}",
  "messages": [{"role": "user", "content": "{{prompt}}"}],
  "temperature": 1.0,
  "max_tokens": 1024
}
```

---

## 2. Google Gemini API

**엔드포인트:** `POST https://generativelanguage.googleapis.com/v1beta/models/{{model}}:generateContent`

### 2.1 조정 가능 파라미터 (generationConfig)

| 파라미터 | 타입 | 범위/기본값 | 설명 |
|---|---|---|---|
| `model` | string | — (필수) | 모델명 (예: `gemini-2.0-flash`, `gemini-1.5-pro`, `gemini-1.5-flash`) |
| `temperature` | number | 0.0 ~ 2.0, 모델별 기본값 | 출력의 무작위성 제어 |
| `topP` | number | 0.0 ~ 1.0, 모델별 기본값 | 핵 샘플링. 누적 확률이 topP 이하인 토큰만 샘플링 |
| `topK` | integer | 모델별 기본값 | 샘플링 시 고려할 최대 토큰 수. topK가 비어있으면 모델이 top-k 샘플링을 적용하지 않음 |
| `maxOutputTokens` | integer | 모델별 한계 | 응답에 포함할 최대 토큰 수 |
| `candidateCount` | integer | 기본값 1 | 반환할 응답 후보 수 (이전 모델에서만 작동) |
| `stopSequences` | string[] | 최대 5개 | 출력 생성을 중지할 문자열 시퀀스 집합 |
| `presencePenalty` | number | — | 토큰이 이미 응답에 등장했을 때 적용되는 페널티. 어휘 다양성 증가 |
| `frequencyPenalty` | number | — | 토큰이 등장한 횟수에 비례하는 페널티. 반복 감소 |
| `seed` | integer | — | 디코딩 시드값. 설정하지 않으면 무작위 생성 |
| `responseMimeType` | string | — | 출력 MIME 타입. `text/plain` (기본), `application/json`, `text/x.enum` |
| `responseSchema` | object (Schema) | — | JSON 모드 시 출력 스키마. OpenAPI 스키마 하위 집합 |
| `thinkingConfig` | object | — | 사고 기능 설정. `{ "includeThoughts": bool, "thinkingBudget": int, "thinkingLevel": "MINIMAL"\|"LOW"\|"MEDIUM"\|"HIGH" }` |

### 2.2 인증 방식

```
URL 파라미터: ?key={API_KEY}
또는
헤더: x-goog-api-key: {API_KEY}
```

### 2.3 기본 요청 바디 템플릿

```json
{
  "contents": [{"parts": [{"text": "{{prompt}}"}]}],
  "generationConfig": {
    "temperature": 1.0,
    "maxOutputTokens": 1024
  }
}
```

---

## 3. Anthropic Claude API

**엔드포인트:** `POST https://api.anthropic.com/v1/messages`

### 3.1 조정 가능 파라미터

| 파라미터 | 타입 | 범위/기본값 | 설명 |
|---|---|---|---|
| `model` | string | — (필수) | 모델명 (예: `claude-opus-4-7`, `claude-sonnet-4-6`, `claude-haiku-4-5-20251001`) |
| `max_tokens` | integer | — (필수) | 생성할 최대 토큰 수 |
| `temperature` | number | 0.0 ~ 1.0, 기본값 1.0 | 출력의 무작위성 제어. **⚠️ Deprecated — output_config.effort 사용 권장** |
| `top_p` | number | — | 핵 샘플링. **⚠️ Deprecated** |
| `top_k` | number | — | 상위 K개 토큰만 샘플링. **⚠️ Deprecated** |
| `stop_sequences` | string[] | 최대 4개 | 생성을 중지할 문자열 시퀀스 |
| `system` | string/array | — | 시스템 프롬프트 |
| `thinking` | object | — | 확장 사고 설정. `{ "type": "enabled", "budget_tokens": int }` 또는 `{ "type": "adaptive" }` |
| `output_config` | object | — | 출력 설정. `{ "effort": "low"\|"medium"\|"high", "format": { "type": "json_schema", "schema": {...} } }` |
| `tool_choice` | object | — | 도구 사용 방식. `auto`, `any`, `none`, 또는 특정 도구 지정 |
| `metadata` | object | — | 요청 메타데이터. `{ "user_id": "..." }` |

### 3.2 인증 방식

```
헤더:
  x-api-key: {API_KEY}
  anthropic-version: 2023-06-01
```

### 3.3 기본 요청 바디 템플릿

```json
{
  "model": "{{model}}",
  "max_tokens": 1024,
  "messages": [{"role": "user", "content": "{{prompt}}"}]
}
```

---

## 4. 공통 파라미터 비교

| 파라미터 | OpenAI | Gemini | Claude | 비고 |
|---|---|---|---|---|
| **모델 선택** | `model` | `model` (URL 경로) | `model` | ✅ 모두 지원 |
| **temperature** | 0.0~2.0 | 0.0~2.0 | 0.0~1.0 | ⚠️ 범위 다름 (Claude 최대 1.0) |
| **top_p** | 0.0~1.0 | 0.0~1.0 | Deprecated | Claude는 권장하지 않음 |
| **top_k** | ❌ | ✅ | Deprecated | Gemini만 정식 지원 |
| **max_tokens** | ✅ | `maxOutputTokens` | ✅ (필수) | Claude는 필수 파라미터 |
| **stop sequences** | `stop` | `stopSequences` | `stop_sequences` | ✅ 모두 지원, 최대 개수 다름 |
| **presence_penalty** | ✅ | ✅ | ❌ | OpenAI/Gemini만 |
| **frequency_penalty** | ✅ | ✅ | ❌ | OpenAI/Gemini만 |
| **seed** | ✅ | ✅ | ❌ | OpenAI/Gemini만 |
| **JSON 모드** | `response_format` | `responseMimeType` + `responseSchema` | `output_config.format` | ✅ 모두 지원, 방식 다름 |
| **사고(Thinking)** | ❌ (reasoning 모델은 별도) | `thinkingConfig` | `thinking` | Gemini/Claude 지원 |
| **시스템 프롬프트** | `messages[0].role=system` | `systemInstruction` | `system` | ✅ 모두 지원, 위치 다름 |

---

## 5. PromptDeck Provider 설정에 반영할 사항

### 5.1 Provider별 기본 파라미터 세트

Provider 설정 페이지에서 providerType에 따라 조정 가능한 파라미터를 다르게 표시해야 합니다:

**OPENAI:**
- model (select)
- temperature (slider, 0.0~2.0)
- max_tokens (number)
- top_p (slider, 0.0~1.0)
- presence_penalty (slider, -2.0~2.0)
- frequency_penalty (slider, -2.0~2.0)
- stop (text, 최대 4개)
- response_format (select: text / json)

**GEMINI:**
- model (select)
- temperature (slider, 0.0~2.0)
- maxOutputTokens (number)
- topP (slider, 0.0~1.0)
- topK (number)
- presencePenalty (slider)
- frequencyPenalty (slider)
- stopSequences (text, 최대 5개)
- responseMimeType (select: text/plain / application/json)

**CLAUDE:**
- model (select)
- max_tokens (number, 필수)
- temperature (slider, 0.0~1.0) — Deprecated but still available
- stop_sequences (text, 최대 4개)
- system (textarea)

### 5.2 bodyTemplateJson에 포함할 파라미터

현재 PR #23의 `PROVIDER_PRESETS` 바디 템플릿은 최소한의 파라미터만 포함하고 있습니다. 사용자가 조정 가능한 파라미터를 템플릿에 반영하려면:

```json
// OPENAI 예시
{
  "model": "{{model}}",
  "messages": [{"role": "user", "content": "{{prompt}}"}],
  "temperature": {{temperature}},
  "max_tokens": {{max_tokens}},
  "top_p": {{top_p}}
}
```

```json
// GEMINI 예시
{
  "contents": [{"parts": [{"text": "{{prompt}}"}]}],
  "generationConfig": {
    "temperature": {{temperature}},
    "maxOutputTokens": {{max_output_tokens}},
    "topP": {{top_p}},
    "topK": {{top_k}}
  }
}
```

```json
// CLAUDE 예시
{
  "model": "{{model}}",
  "max_tokens": {{max_tokens}},
  "messages": [{"role": "user", "content": "{{prompt}}"}],
  "temperature": {{temperature}}
}
```

### 5.3 인증 방식별 차이

| Provider | 인증 방식 | PromptDeck 설정 |
|---|---|---|
| OPENAI | `Authorization: Bearer {key}` | `authType: BEARER` |
| GEMINI | URL 파라미터 `?key={key}` | `authType: QUERY_PARAM`, `authQueryParamName: key` |
| CLAUDE | `x-api-key: {key}` + `anthropic-version: 2023-06-01` | `authType: HEADER`, `authHeaderName: x-api-key` |

> **참고:** Claude는 `anthropic-version` 헤더도 필수입니다. 현재 PR #23의 프리셋에는 이 헤더가 누락되어 있으므로, v3에서 추가해야 합니다.