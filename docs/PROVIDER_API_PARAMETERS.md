# Provider API 조정 가능 파라미터 정리

> 작성일: 2026-05-13 (최신 모델 기준 업데이트)  
> 출처: 각 Provider 공식 API 문서  
> 목적: PromptDeck Provider 설정 페이지에서 사용자가 조정할 수 있는 파라미터 식별

---

## 1. OpenAI (Responses API / Chat Completions API)

**최신 모델:** GPT-5.5, GPT-5.4, GPT-5.4-mini  
**엔드포인트:**  
- Responses API (권장): `POST https://api.openai.com/v1/responses`  
- Chat Completions API (레거시): `POST https://api.openai.com/v1/chat/completions`

> ⚠️ OpenAI는 Responses API 사용을 권장합니다. Chat Completions API도 여전히 지원됩니다.

### 1.1 조정 가능 파라미터

| 파라미터 | 타입 | 범위/기본값 | 설명 |
|---|---|---|---|
| `model` | string | — (필수) | 사용할 모델 ID (예: `gpt-5.5`, `gpt-5.4`, `gpt-5.4-mini`, `gpt-4o`, `gpt-4o-mini`) |
| `temperature` | number | 0.0 ~ 2.0, 기본값 1.0 | 출력의 무작위성 제어. 낮을수록 결정적, 높을수록 창의적 |
| `top_p` | number | 0.0 ~ 1.0, 기본값 1.0 | 핵 샘플링(nucleus sampling). temperature와 함께 사용 시 temperature 또는 top_p 중 하나만 권장 |
| `max_output_tokens` | integer | 16 ~ 모델 한계 | 생성할 최대 토큰 수 (출력 + 추론 토큰 포함). Responses API에서 사용 |
| `max_tokens` | integer | 1 ~ 모델 한계 | 생성할 최대 토큰 수. Chat Completions API에서 사용 |
| `stop` | string/array | 최대 4개 시퀀스 | 생성을 중지할 문자열 시퀀스 |
| `presence_penalty` | number | -2.0 ~ 2.0, 기본값 0 | 이미 등장한 토큰의 등장 여부에 따른 페널티. 어휘 다양성 증가 |
| `frequency_penalty` | number | -2.0 ~ 2.0, 기본값 0 | 이미 등장한 토큰의 빈도에 비례하는 페널티. 반복 감소 |
| `seed` | integer | — | 결과 재현성을 위한 시드값 |
| `logprobs` | boolean | 기본값 false | 출력 토큰의 로그 확률 반환 여부 |
| `top_logprobs` | integer | 0 ~ 20 | logprobs=true 시 각 토큰별 반환할 상위 로그 확률 수 |
| `truncation` | string | "auto" \| "disabled" | 컨텍스트 윈도우 초과 시 자동 잘라내기 여부 |

### 1.2 🆕 GPT-5.x 전용: Reasoning (추론) 파라미터

GPT-5.5, GPT-5.4 등 reasoning 모델에서 지원:

| 파라미터 | 타입 | 값 | 설명 |
|---|---|---|---|
| `reasoning.effort` | string | `none` \| `minimal` \| `low` \| `medium` \| `high` \| `xhigh` | 모델의 추론 노력 수준. GPT-5.5 기본값: `medium`. 낮을수록 빠르고 저렴, 높을수록 깊은 추론 |
| `reasoning.summary` | string | `auto` \| `concise` \| `detailed` | 추론 과정 요약 반환 방식. `auto`는 모델이 자동 선택 |

**reasoning.effort별 권장 용도:**

| 수준 | 용도 |
|---|---|
| none | 지연 시간이 중요한 단순 작업 (분류, 빠른 정보 검색) |
| low | 도구 사용, 계획, 검색이 필요한 작업 (데이터 분석, 초안 작성, 고객 지원) |
| medium | 복잡한 추론과 판단이 필요한 작업 (에이전트 코딩, 연구) — **GPT-5.5 기본값** |
| high | 복잡한 디버깅, 깊은 계획, 고품질이 중요한 작업 |
| xhigh | 심층 연구, 비동기 워크플로우, 매우 복잡한 코딩 |

### 1.3 🆕 Responses API 전용 파라미터

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `instructions` | string | 시스템/개발자 프롬프트. `previous_response_id` 사용 시 이전 instructions은 전달되지 않음 |
| `text.format` | object | 출력 형식. `{ "type": "text" }` 또는 `{ "type": "json_schema", "schema": {...} }` |
| `text.verbosity` | string | 출력 상세도 |
| `previous_response_id` | string | 이전 응답 ID. 대화 상태 유지에 사용 |
| `prompt` | object | 재사용 가능한 프롬프트 템플릿 참조 (`id`, `version`, `variables`) |
| `background` | boolean | 백그라운드에서 응답 생성 여부 |
| `service_tier` | string | `auto` \| `default` \| `flex` \| `priority` | 처리 우선순위 |

### 1.4 인증 방식

```
Authorization: Bearer {API_KEY}
```

### 1.5 기본 요청 바디 템플릿

**Chat Completions API (PromptDeck 현재 방식):**
```json
{
  "model": "{{model}}",
  "messages": [{"role": "user", "content": "{{prompt}}"}],
  "temperature": 1.0,
  "max_tokens": 1024
}
```

**Responses API (향후 마이그레이션 권장):**
```json
{
  "model": "gpt-5.5",
  "input": "{{prompt}}",
  "instructions": "{{system_prompt}}",
  "reasoning": { "effort": "medium" },
  "max_output_tokens": 1024,
  "temperature": 1.0
}
```

---

## 2. Google Gemini API

**최신 모델:** Gemini 3.1 Pro, Gemini 3 Flash, Gemini 2.5 Pro, Gemini 2.5 Flash  
**엔드포인트:** `POST https://generativelanguage.googleapis.com/v1beta/models/{{model}}:generateContent`

### 2.1 조정 가능 파라미터 (generationConfig)

| 파라미터 | 타입 | 범위/기본값 | 설명 |
|---|---|---|---|
| `model` | string | — (필수) | 모델명 (예: `gemini-3.1-pro`, `gemini-3-flash-preview`, `gemini-2.5-pro`, `gemini-2.5-flash`) |
| `temperature` | number | 0.0 ~ 2.0, 모델별 기본값 | 출력의 무작위성 제어. Gemini 3 모델은 기본값 1.0 유지 권장 |
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
| `responseJsonSchema` | object | — | JSON 스키마를 직접 전달하는 `responseSchema`의 대안. `$id`, `$defs`, `$ref` 등 지원 |
| `responseModalities` | enum[] | `TEXT` \| `IMAGE` \| `AUDIO` | 요청된 응답 모달리티 |

### 2.2 🆕 Gemini 3 전용: 사고(Thinking) 파라미터

Gemini 3 시리즈부터 `thinkingLevel`을 사용한 사고 제어가 권장됩니다:

| 파라미터 | 타입 | 값 | 설명 |
|---|---|---|---|
| `thinkingConfig.thinkingLevel` | string | `MINIMAL` \| `LOW` \| `MEDIUM` \| `HIGH` | **Gemini 3 전용 (권장)**. 추론 깊이 제어. 기본값: `HIGH` |
| `thinkingConfig.includeThoughts` | boolean | true \| false | 사고 요약 반환 여부 |

**thinkingLevel별 동작:**

| 수준 | Gemini 3 Pro | Gemini 3 Flash | 설명 |
|---|---|---|---|
| MINIMAL | 지원 안 함 | 지원 (기본값) | 대부분 쿼리에서 '사고 없음'과 유사. 지연 시간 최소화 |
| LOW | 지원 | 지원 | 간단한 지시 따르기, 채팅, 고처리량 애플리케이션에 적합 |
| MEDIUM | 지원 | 지원 | 대부분의 작업에 균형 잡힌 사고 |
| HIGH | 지원 (기본값, 동적) | 지원 (기본값, 동적) | 추론 깊이 극대화. 복잡한 문제에 적합 |

> ⚠️ Gemini 3.1 Pro에서는 사고를 사용 중지할 수 없습니다.

### 2.3 Gemini 2.5 전용: 사고 예산 (thinkingBudget)

Gemini 2.5 시리즈는 `thinkingBudget`을 사용합니다 (Gemini 3에서는 권장하지 않음):

| 파라미터 | 타입 | 값 | 설명 |
|---|---|---|---|
| `thinkingConfig.thinkingBudget` | integer | 0 ~ 32768 (모델별 상이) | **Gemini 2.5 전용**. 사고에 사용할 토큰 수. `0` = 사고 중지, `-1` = 동적 사고 |

**모델별 thinkingBudget 범위:**

| 모델 | 기본값 | 범위 | 사고 중지 가능 |
|---|---|---|---|
| 2.5 Pro | -1 (동적) | 128 ~ 32768 | ❌ 불가 |
| 2.5 Flash | -1 (동적) | 0 ~ 24576 | ✅ (thinkingBudget=0) |
| 2.5 Flash Lite | -1 | 512 ~ 24576 | ✅ (thinkingBudget=0) |

### 2.4 🆕 사고 서명 (Thinking Signature)

Gemini 2.5/3 모델은 멀티턴 대화에서 사고 컨텍스트를 유지하기 위해 **사고 서명**을 반환합니다:
- 함수 호출 시 사고 서명이 포함된 파트를 그대로 다시 전달해야 함
- 서명은 암호화된 내부 추론 표현이며, 해석하거나 수정하면 안 됨
- Gemini 3은 모든 유형의 파트에 대해 사고 서명을 반환할 수 있음

### 2.5 인증 방식

```
URL 파라미터: ?key={API_KEY}
또는
헤더: x-goog-api-key: {API_KEY}
```

### 2.6 기본 요청 바디 템플릿

**Gemini 3 (thinkingLevel 사용):**
```json
{
  "contents": [{"parts": [{"text": "{{prompt}}"}]}],
  "generationConfig": {
    "temperature": 1.0,
    "maxOutputTokens": 1024
  },
  "thinkingConfig": {
    "thinkingLevel": "HIGH",
    "includeThoughts": true
  }
}
```

**Gemini 2.5 (thinkingBudget 사용):**
```json
{
  "contents": [{"parts": [{"text": "{{prompt}}"}]}],
  "generationConfig": {
    "temperature": 1.0,
    "maxOutputTokens": 1024
  },
  "thinkingConfig": {
    "thinkingBudget": -1
  }
}
```

---

## 3. Anthropic Claude API

**최신 모델:** Claude Opus 4.7, Claude Opus 4.6, Claude Sonnet 4.6, Claude Haiku 4.5  
**엔드포인트:** `POST https://api.anthropic.com/v1/messages`

### 3.1 조정 가능 파라미터

| 파라미터 | 타입 | 범위/기본값 | 설명 |
|---|---|---|---|
| `model` | string | — (필수) | 모델명 (예: `claude-opus-4-7`, `claude-opus-4-6`, `claude-sonnet-4-6`, `claude-haiku-4-5-20251001`) |
| `max_tokens` | integer | — (필수) | 생성할 최대 토큰 수. Opus 4.7/4.6: 최대 128k, Sonnet 4.6/Haiku 4.5: 최대 64k |
| `temperature` | number | 0.0 ~ 1.0, 기본값 1.0 | 출력의 무작위성 제어. **⚠️ Deprecated — output_config.effort 사용 권장** |
| `top_p` | number | — | 핵 샘플링. **⚠️ Deprecated**. 사고 활성화 시 1.0~0.95만 허용 |
| `top_k` | number | — | 상위 K개 토큰만 샘플링. **⚠️ Deprecated** |
| `stop_sequences` | string[] | 최대 4개 | 생성을 중지할 문자열 시퀀스 |
| `system` | string/array | — | 시스템 프롬프트 |
| `metadata` | object | — | 요청 메타데이터. `{ "user_id": "..." }` |
| `tool_choice` | object | — | 도구 사용 방식. `auto`, `any`, `none`, 또는 특정 도구 지정 |

### 3.2 🆕 Claude Opus 4.7 전용: 적응형 사고 (Adaptive Thinking)

**Claude Opus 4.7에서는 수동 사고(`thinking: {type: "enabled", budget_tokens: N}`)가 더 이상 지원되지 않으며, 적응형 사고만 사용 가능합니다.**

| 파라미터 | 타입 | 값 | 설명 |
|---|---|---|---|
| `thinking.type` | string | `"adaptive"` | **Opus 4.7 필수**. 모델이 요청 복잡성에 따라 사고 깊이를 자동 조절 |
| `thinking.display` | string | `"summarized"` \| `"omitted"` | 사고 내용 표시 방식. Opus 4.7 기본값: `"omitted"`. 요약을 받으려면 `"summarized"` 명시 필요 |

### 3.3 🆕 Claude Opus 4.6 / Sonnet 4.6: 적응형 사고 (권장)

Opus 4.6과 Sonnet 4.6에서는 적응형 사고가 권장되며, 수동 사고는 **Deprecated**입니다:

| 파라미터 | 타입 | 값 | 설명 |
|---|---|---|---|
| `thinking.type` | string | `"adaptive"` (권장) \| `"enabled"` (Deprecated) | `"adaptive"` 권장. 수동 모드는 향후 제거 예정 |
| `thinking.budget_tokens` | integer | 1024 ~ 128k | 수동 모드(`"enabled"`) 시 사고 토큰 예산. **Deprecated** |
| `thinking.display` | string | `"summarized"` \| `"omitted"` | 사고 내용 표시 방식. 기본값: `"summarized"` |

### 3.4 🆕 output_config: 출력 제어

| 파라미터 | 타입 | 값 | 설명 |
|---|---|---|---|
| `output_config.effort` | string | `"low"` \| `"medium"` \| `"high"` | 모델의 추론 노력 수준. `temperature`를 대체하는 권장 파라미터 |
| `output_config.format` | object | `{ "type": "text" }` \| `{ "type": "json_schema", "schema": {...} }` | 출력 형식 지정. JSON 구조화 출력 가능 |

### 3.5 모델별 사고 지원 현황

| 모델 | 수동 사고 (`enabled`) | 적응형 사고 (`adaptive`) | 사고 중지 가능 | 기본 사고 수준 |
|---|---|---|---|---|
| Claude Opus 4.7 | ❌ (400 에러) | ✅ (유일한 방식) | ❌ | 동적 (adaptive) |
| Claude Mythos Preview | ✅ (지원) | ✅ (기본값) | ❌ | 동적 (adaptive) |
| Claude Opus 4.6 | ✅ (Deprecated) | ✅ (권장) | ✅ (`disabled`) | 동적 |
| Claude Sonnet 4.6 | ✅ (Deprecated) | ✅ (권장) | ✅ (`disabled`) | 동적 |
| Claude Haiku 4.5 | ✅ | ❌ | ✅ (`disabled`) | — |

### 3.6 인증 방식

```
헤더:
  x-api-key: {API_KEY}
  anthropic-version: 2023-06-01
```

> ⚠️ `anthropic-version` 헤더가 필수입니다. 현재 PR #23의 CLAUDE 프리셋에 이 헤더가 누락되어 있습니다.

### 3.7 기본 요청 바디 템플릿

**Claude Opus 4.7 (적응형 사고):**
```json
{
  "model": "claude-opus-4-7",
  "max_tokens": 1024,
  "thinking": { "type": "adaptive" },
  "messages": [{"role": "user", "content": "{{prompt}}"}]
}
```

**Claude Opus 4.6 / Sonnet 4.6 (적응형 사고, 권장):**
```json
{
  "model": "claude-opus-4-6",
  "max_tokens": 1024,
  "thinking": { "type": "adaptive" },
  "output_config": { "effort": "medium" },
  "messages": [{"role": "user", "content": "{{prompt}}"}]
}
```

**Claude Haiku 4.5 (수동 사고):**
```json
{
  "model": "claude-haiku-4-5-20251001",
  "max_tokens": 1024,
  "thinking": { "type": "enabled", "budget_tokens": 10000 },
  "messages": [{"role": "user", "content": "{{prompt}}"}]
}
```

---

## 4. 공통 파라미터 비교

| 파라미터 | OpenAI | Gemini | Claude | 비고 |
|---|---|---|---|---|
| **모델 선택** | `model` | `model` (URL 경로) | `model` | ✅ 모두 지원 |
| **temperature** | 0.0~2.0 | 0.0~2.0 | 0.0~1.0 | ⚠️ 범위 다름 (Claude 최대 1.0). Claude에서는 Deprecated |
| **top_p** | 0.0~1.0 | 0.0~1.0 | Deprecated | Claude는 권장하지 않음 |
| **top_k** | ❌ | ✅ | Deprecated | Gemini만 정식 지원 |
| **max_tokens** | `max_output_tokens` / `max_tokens` | `maxOutputTokens` | `max_tokens` (필수) | Claude는 필수. OpenAI는 reasoning 토큰 포함 |
| **stop sequences** | `stop` | `stopSequences` | `stop_sequences` | ✅ 모두 지원, 최대 개수 다름 |
| **presence_penalty** | ✅ | ✅ | ❌ | OpenAI/Gemini만 |
| **frequency_penalty** | ✅ | ✅ | ❌ | OpenAI/Gemini만 |
| **seed** | ✅ | ✅ | ❌ | OpenAI/Gemini만 |
| **JSON 모드** | `response_format` / `text.format` | `responseMimeType` + `responseSchema` | `output_config.format` | ✅ 모두 지원, 방식 다름 |
| **🆕 추론/사고 제어** | `reasoning.effort` | `thinkingConfig.thinkingLevel` / `thinkingBudget` | `thinking` + `output_config.effort` | ⚠️ 각 Provider마다 방식이 완전히 다름 |
| **🆕 추론 요약** | `reasoning.summary` | `thinkingConfig.includeThoughts` | `thinking.display` | OpenAI/Gemini는 요약, Claude는 요약/생략 선택 |
| **시스템 프롬프트** | `instructions` / `messages[0].role=system` | `systemInstruction` | `system` | ✅ 모두 지원, 위치 다름 |
| **🆕 출력 상세도** | `text.verbosity` | ❌ | ❌ | OpenAI Responses API 전용 |

---

## 5. PromptDeck Provider 설정에 반영할 사항

### 5.1 Provider별 기본 파라미터 세트

Provider 설정 페이지에서 providerType에 따라 조정 가능한 파라미터를 다르게 표시해야 합니다:

**OPENAI (GPT-5.5 등):**
- model (select: gpt-5.5, gpt-5.4, gpt-5.4-mini, gpt-4o, gpt-4o-mini)
- temperature (slider, 0.0~2.0)
- max_output_tokens (number)
- top_p (slider, 0.0~1.0)
- presence_penalty (slider, -2.0~2.0)
- frequency_penalty (slider, -2.0~2.0)
- stop (text, 최대 4개)
- response_format (select: text / json)
- 🆕 reasoning.effort (select: none / minimal / low / medium / high / xhigh) — GPT-5.x 전용
- 🆕 reasoning.summary (select: auto / concise / detailed) — GPT-5.x 전용

**GEMINI (3.1 Pro 등):**
- model (select: gemini-3.1-pro, gemini-3-flash-preview, gemini-2.5-pro, gemini-2.5-flash)
- temperature (slider, 0.0~2.0)
- maxOutputTokens (number)
- topP (slider, 0.0~1.0)
- topK (number)
- presencePenalty (slider)
- frequencyPenalty (slider)
- stopSequences (text, 최대 5개)
- responseMimeType (select: text/plain / application/json)
- 🆕 thinkingLevel (select: MINIMAL / LOW / MEDIUM / HIGH) — Gemini 3 전용
- 🆕 thinkingBudget (number, 0~32768) — Gemini 2.5 전용
- 🆕 includeThoughts (boolean) — 사고 요약 반환 여부

**CLAUDE (Opus 4.7 등):**
- model (select: claude-opus-4-7, claude-opus-4-6, claude-sonnet-4-6, claude-haiku-4-5-20251001)
- max_tokens (number, 필수)
- 🆕 thinking.type (select: adaptive / enabled / disabled) — 모델별 지원 상이
- 🆕 thinking.budget_tokens (number, 1024~128k) — 수동 모드 전용, Deprecated
- 🆕 thinking.display (select: summarized / omitted) — 사고 내용 표시 방식
- 🆕 output_config.effort (select: low / medium / high) — temperature 대체 권장
- 🆕 output_config.format (select: text / json_schema) — 구조화 출력
- temperature (slider, 0.0~1.0) — ⚠️ Deprecated
- stop_sequences (text, 최대 4개)
- system (textarea)

### 5.2 bodyTemplateJson에 포함할 파라미터

현재 PR #23의 `PROVIDER_PRESETS` 바디 템플릿은 최소한의 파라미터만 포함하고 있습니다. 사용자가 조정 가능한 파라미터를 템플릿에 반영하려면:

```json
// OPENAI (GPT-5.5) 예시 — Chat Completions API
{
  "model": "{{model}}",
  "messages": [{"role": "user", "content": "{{prompt}}"}],
  "temperature": {{temperature}},
  "max_tokens": {{max_tokens}},
  "top_p": {{top_p}}
}
```

```json
// OPENAI (GPT-5.5) 예시 — Responses API (향후 마이그레이션)
{
  "model": "gpt-5.5",
  "input": "{{prompt}}",
  "instructions": "{{system_prompt}}",
  "reasoning": { "effort": "medium" },
  "max_output_tokens": {{max_output_tokens}},
  "temperature": {{temperature}}
}
```

```json
// GEMINI (3.1 Pro) 예시
{
  "contents": [{"parts": [{"text": "{{prompt}}"}]}],
  "generationConfig": {
    "temperature": {{temperature}},
    "maxOutputTokens": {{max_output_tokens}},
    "topP": {{top_p}},
    "topK": {{top_k}}
  },
  "thinkingConfig": {
    "thinkingLevel": "HIGH",
    "includeThoughts": true
  }
}
```

```json
// CLAUDE (Opus 4.7) 예시 — 적응형 사고
{
  "model": "claude-opus-4-7",
  "max_tokens": {{max_tokens}},
  "thinking": { "type": "adaptive" },
  "messages": [{"role": "user", "content": "{{prompt}}"}]
}
```

```json
// CLAUDE (Sonnet 4.6) 예시 — 적응형 사고 + 출력 제어
{
  "model": "claude-sonnet-4-6",
  "max_tokens": {{max_tokens}},
  "thinking": { "type": "adaptive" },
  "output_config": { "effort": "medium" },
  "messages": [{"role": "user", "content": "{{prompt}}"}]
}
```

### 5.3 인증 방식별 차이

| Provider | 인증 방식 | PromptDeck 설정 |
|---|---|---|
| OPENAI | `Authorization: Bearer {key}` | `authType: BEARER` |
| GEMINI | URL 파라미터 `?key={key}` | `authType: QUERY_PARAM`, `authQueryParamName: key` |
| CLAUDE | `x-api-key: {key}` + `anthropic-version: 2023-06-01` | `authType: HEADER`, `authHeaderName: x-api-key` |

> **⚠️ 중요:** Claude는 `anthropic-version` 헤더도 필수입니다. 현재 PR #23의 프리셋에 이 헤더가 누락되어 있으므로, v3에서 추가해야 합니다.

### 5.4 🆕 v3에서 추가로 고려해야 할 사항

1. **Reasoning/Thinking 파라미터 UI** — 각 Provider마다 사고 제어 방식이 완전히 다름:
   - OpenAI: `reasoning.effort` (none/minimal/low/medium/high/xhigh)
   - Gemini 3: `thinkingConfig.thinkingLevel` (MINIMAL/LOW/MEDIUM/HIGH)
   - Gemini 2.5: `thinkingConfig.thinkingBudget` (0~32768)
   - Claude Opus 4.7: `thinking.type: "adaptive"` (유일한 옵션)
   - Claude Opus/Sonnet 4.6: `thinking.type: "adaptive"` (권장) 또는 `"enabled"` (Deprecated)

2. **모델별 파라미터 가시성** — 모델 선택에 따라 사용 가능한 파라미터가 다름:
   - GPT-5.x: `reasoning.effort` 표시
   - GPT-4o: `reasoning.effort` 숨김
   - Gemini 3.x: `thinkingLevel` 표시, `thinkingBudget` 숨김
   - Gemini 2.5: `thinkingBudget` 표시, `thinkingLevel` 숨김
   - Claude Opus 4.7: `thinking.type: "adaptive"` 고정, `budget_tokens` 숨김
   - Claude Opus/Sonnet 4.6: `thinking.type` 선택, `budget_tokens` 표시

3. **Deprecated 파라미터 처리** — `temperature`, `top_p`, `top_k`는 Claude에서 Deprecated. UI에서 경고 표시 필요

4. **Claude 필수 헤더** — `anthropic-version: 2023-06-01` 헤더를 요청에 포함해야 함