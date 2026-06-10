import { Button, FormField, Tabs } from '../ui'

export default function ProviderSettingForm({
  mode,
  form,
  tab,
  saving,
  error,
  organizations,
  providerTypes,
  httpMethods,
  authTypes,
  availableModels,
  onTabChange,
  onProviderTypeChange,
  onFieldChange,
  onSubmit,
  onCancel,
  styles
}) {
  const isCustom = form.providerType === 'CUSTOM'

  return (
    <>
      <Tabs
        tabs={[
          { key: 'basic', label: '기본 정보' },
          ...(isCustom ? [{ key: 'advanced', label: '고급 설정' }] : [])
        ]}
        activeTab={tab}
        onChange={onTabChange}
      />

      <form className="pd-card pd-stagger-1" onSubmit={onSubmit} style={styles.form}>
        {tab === 'basic' && (
          <BasicFields
            mode={mode}
            form={form}
            organizations={organizations}
            providerTypes={providerTypes}
            httpMethods={httpMethods}
            authTypes={authTypes}
            availableModels={availableModels}
            onProviderTypeChange={onProviderTypeChange}
            onFieldChange={onFieldChange}
            styles={styles}
          />
        )}

        {tab === 'advanced' && isCustom && (
          <AdvancedFields form={form} onFieldChange={onFieldChange} styles={styles} />
        )}

        {error && <p style={styles.error}>{error}</p>}
        <div style={styles.formActions}>
          <Button type="submit" loading={saving}>
            {mode === 'create' ? '생성' : '수정'}
          </Button>
          <Button variant="outline" type="button" onClick={onCancel}>취소</Button>
        </div>
      </form>
    </>
  )
}

function BasicFields({
  mode,
  form,
  organizations,
  providerTypes,
  httpMethods,
  authTypes,
  availableModels,
  onProviderTypeChange,
  onFieldChange,
  styles
}) {
  const isCustom = form.providerType === 'CUSTOM'

  return (
    <>
      <FormField label="Provider 타입">
        <select style={styles.input} value={form.providerType} onChange={onProviderTypeChange}>
          {providerTypes.map(type => <option key={type}>{type}</option>)}
        </select>
      </FormField>
      <FormField label="조직" hint={mode === 'edit' ? '수정 시 조직은 변경되지 않습니다' : '선택하지 않으면 개인 설정으로 저장됩니다'}>
        <select
          style={{ ...styles.input, ...(mode === 'edit' ? styles.inputLocked : {}) }}
          value={form.organizationId}
          onChange={onFieldChange('organizationId')}
          disabled={mode === 'edit'}
        >
          <option value="">개인</option>
          {organizations.map(org => (
            <option key={org.id} value={org.id}>{org.name}</option>
          ))}
        </select>
      </FormField>
      <FormField label="표시 이름">
        <input style={styles.input} required value={form.displayName} onChange={onFieldChange('displayName')} placeholder="My GPT-4 Setting" />
      </FormField>
      <FormField label="모델명">
        {isCustom ? (
          <input style={styles.input} required value={form.model} onChange={onFieldChange('model')} placeholder="your-model-name" />
        ) : (
          <select style={styles.input} value={form.model} onChange={onFieldChange('model')}>
            {availableModels.map(model => <option key={model}>{model}</option>)}
          </select>
        )}
      </FormField>
      {isCustom && (
        <CustomBasicFields form={form} httpMethods={httpMethods} authTypes={authTypes} onFieldChange={onFieldChange} styles={styles} />
      )}
    </>
  )
}

function CustomBasicFields({ form, httpMethods, authTypes, onFieldChange, styles }) {
  return (
    <>
      <FormField label="엔드포인트 URL">
        <input
          style={styles.input}
          required
          value={form.endpoint}
          onChange={onFieldChange('endpoint')}
          placeholder="https://api.example.com/v1/chat"
        />
      </FormField>
      <FormField label="HTTP 메서드">
        <select style={styles.input} value={form.method} onChange={onFieldChange('method')}>
          {httpMethods.map(method => <option key={method}>{method}</option>)}
        </select>
      </FormField>
      <FormField label="인증 방식">
        <select style={styles.input} value={form.authType} onChange={onFieldChange('authType')}>
          {authTypes.map(type => <option key={type}>{type}</option>)}
        </select>
      </FormField>
      {form.authType === 'HEADER' && (
        <FormField label="인증 헤더명">
          <input style={styles.input} value={form.authHeaderName} onChange={onFieldChange('authHeaderName')} placeholder="X-API-Key" />
        </FormField>
      )}
      {form.authType === 'QUERY_PARAM' && (
        <FormField label="인증 쿼리 파라미터명">
          <input style={styles.input} value={form.authQueryParamName} onChange={onFieldChange('authQueryParamName')} placeholder="api_key" />
        </FormField>
      )}
      <FormField label="응답 추출 경로 (JSONPath)">
        <input style={styles.input} value={form.responsePath} onChange={onFieldChange('responsePath')} placeholder="choices[0].message.content" />
      </FormField>
    </>
  )
}

function AdvancedFields({ form, onFieldChange, styles }) {
  return (
    <>
      <FormField label="헤더 JSON" hint={'예: {"X-Custom": "value"}'}>
        <textarea style={styles.textarea} value={form.headersJson} onChange={onFieldChange('headersJson')} rows={3} />
      </FormField>
      <FormField label="쿼리 파라미터 JSON" hint={'예: {"version": "2024-01"}'}>
        <textarea style={styles.textarea} value={form.queryParamsJson} onChange={onFieldChange('queryParamsJson')} rows={3} />
      </FormField>
      <FormField label="바디 템플릿 JSON" hint="{{prompt}}, {{model}} 등 변수 사용 가능">
        <textarea style={styles.textarea} value={form.bodyTemplateJson} onChange={onFieldChange('bodyTemplateJson')} rows={6} />
      </FormField>
      <FormField label="옵션 스키마 JSON" hint="사용자 정의 옵션 필드 스키마">
        <textarea style={styles.textarea} value={form.optionSchemaJson} onChange={onFieldChange('optionSchemaJson')} rows={4} />
      </FormField>
    </>
  )
}
