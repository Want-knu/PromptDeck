export const PROVIDER_TYPES = ['OPENAI', 'GEMINI', 'CLAUDE', 'CUSTOM']
export const HTTP_METHODS = ['POST', 'GET', 'PUT', 'PATCH', 'DELETE']
export const AUTH_TYPES = ['BEARER', 'HEADER', 'QUERY_PARAM', 'NONE']

// 백엔드 API 연동 전 임시 모델 목록 — 추후 API 응답으로 교체 예정
export const PROVIDER_MODELS = {
  OPENAI: ['gpt-4o', 'gpt-4o-mini', 'gpt-4-turbo', 'gpt-3.5-turbo'],
  GEMINI: ['gemini-2.0-flash', 'gemini-1.5-pro', 'gemini-1.5-flash'],
  CLAUDE: ['claude-opus-4-7', 'claude-sonnet-4-6', 'claude-haiku-4-5-20251001'],
}

// 백엔드 API 연동 전 임시 프리셋 — 추후 API 응답으로 교체 예정
export const PROVIDER_PRESETS = {
  OPENAI: {
    endpoint: 'https://api.openai.com/v1/chat/completions',
    method: 'POST',
    authType: 'BEARER',
    authHeaderName: '',
    authQueryParamName: '',
    responsePath: 'choices[0].message.content',
    bodyTemplateJson: '{\n  "model": "{{model}}",\n  "messages": [{"role": "user", "content": "{{prompt}}"}]\n}',
  },
  GEMINI: {
    endpoint: 'https://generativelanguage.googleapis.com/v1beta/models/{{model}}:generateContent',
    method: 'POST',
    authType: 'QUERY_PARAM',
    authHeaderName: '',
    authQueryParamName: 'key',
    responsePath: 'candidates[0].content.parts[0].text',
    bodyTemplateJson: '{\n  "contents": [{"parts": [{"text": "{{prompt}}"}]}]\n}',
  },
  CLAUDE: {
    endpoint: 'https://api.anthropic.com/v1/messages',
    method: 'POST',
    authType: 'HEADER',
    authHeaderName: 'x-api-key',
    authQueryParamName: '',
    responsePath: 'content[0].text',
    bodyTemplateJson: '{\n  "model": "{{model}}",\n  "max_tokens": 1024,\n  "messages": [{"role": "user", "content": "{{prompt}}"}]\n}',
  },
}
