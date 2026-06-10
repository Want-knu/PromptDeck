import { Button, Card } from '../ui'
import OptionField from './OptionField'

export default function ExecutionConfigPanel({
  form,
  settings,
  keys,
  presets,
  selectedSetting,
  selectedPreset,
  selectedPresetId,
  presetName,
  presetEditorOpen,
  advancedOpen,
  optionSchema,
  optionValues,
  filteredKeys,
  isCustomSetting,
  requiresProviderKey,
  savingPreset,
  presetSaveAction,
  previewing,
  executing,
  canRun,
  error,
  onFormChange,
  onOptionChange,
  onPresetSelect,
  onPresetNameChange,
  onTogglePresetEditor,
  onLoadPreset,
  onCreatePreset,
  onUpdatePreset,
  onDeletePreset,
  onToggleAdvanced,
  onPreview,
  onExecute,
  styles
}) {
  return (
    <Card className="pd-stagger-1" style={styles.panel}>
      <div style={styles.panelHeader}>
        <span style={styles.panelKicker}>Configure</span>
        <h3 style={styles.subheading}>설정 선택</h3>
      </div>

      <PresetSection
        presets={presets}
        selectedPreset={selectedPreset}
        selectedPresetId={selectedPresetId}
        presetName={presetName}
        presetEditorOpen={presetEditorOpen}
        savingPreset={savingPreset}
        presetSaveAction={presetSaveAction}
        providerSettingId={form.providerSettingId}
        onPresetSelect={onPresetSelect}
        onPresetNameChange={onPresetNameChange}
        onTogglePresetEditor={onTogglePresetEditor}
        onLoadPreset={onLoadPreset}
        onCreatePreset={onCreatePreset}
        onUpdatePreset={onUpdatePreset}
        onDeletePreset={onDeletePreset}
        styles={styles}
      />

      <ProviderSettingSelect
        settings={settings}
        selectedSetting={selectedSetting}
        isCustomSetting={isCustomSetting}
        value={form.providerSettingId}
        onChange={value => onFormChange({ providerSettingId: value })}
        styles={styles}
      />

      <ProviderKeySelect
        keys={keys}
        filteredKeys={filteredKeys}
        selectedSetting={selectedSetting}
        requiresProviderKey={requiresProviderKey}
        value={form.providerKeyId}
        onChange={value => onFormChange({ providerKeyId: value })}
        styles={styles}
      />

      <PromptFields form={form} onChange={onFormChange} styles={styles} />

      {Object.keys(optionSchema).length > 0 && (
        <div style={styles.optionSection}>
          <h3 style={styles.optionTitle}>실행 옵션</h3>
          <div style={styles.optionGrid}>
            {Object.entries(optionSchema).map(([name, schema]) => (
              <OptionField
                key={name}
                name={name}
                schema={schema}
                value={optionValues[name]}
                onChange={value => onOptionChange(name, value)}
                styles={styles}
              />
            ))}
          </div>
        </div>
      )}

      <button type="button" style={styles.disclosure} onClick={onToggleAdvanced}>
        {advancedOpen ? '고급 입력 닫기' : '고급 입력 열기'}
      </button>
      {advancedOpen && (
        <>
          <label style={styles.label}>
            추가 변수 (JSON)
            <span style={styles.hint}> — CUSTOM 템플릿이나 추가 치환값이 필요할 때만 사용</span>
          </label>
          <textarea
            style={styles.textarea}
            rows={3}
            placeholder={'{"custom_variable": "value"}'}
            value={form.variables}
            onChange={e => onFormChange({ variables: e.target.value })}
          />
        </>
      )}

      {error && <p style={styles.error}>{error}</p>}

      <div style={styles.btnRow}>
        <Button
          variant="success"
          className={previewing ? 'pd-pulse-soft' : ''}
          onClick={onPreview}
          disabled={previewing || executing || !canRun}
          loading={previewing}
          style={{ flex: 1 }}
        >
          미리보기
        </Button>
        <Button
          className={executing ? 'pd-pulse-soft' : ''}
          onClick={onExecute}
          disabled={previewing || executing || !canRun}
          loading={executing}
          style={{ flex: 1 }}
        >
          실행
        </Button>
      </div>
    </Card>
  )
}

function PresetSection({
  presets,
  selectedPreset,
  selectedPresetId,
  presetName,
  presetEditorOpen,
  savingPreset,
  presetSaveAction,
  providerSettingId,
  onPresetSelect,
  onPresetNameChange,
  onTogglePresetEditor,
  onLoadPreset,
  onCreatePreset,
  onUpdatePreset,
  onDeletePreset,
  styles
}) {
  return (
    <>
      <label style={styles.label}>실행 프리셋</label>
      <div style={styles.presetPanel}>
        <div style={styles.presetRow}>
          <select
            style={styles.select}
            value={selectedPresetId}
            onChange={e => onPresetSelect(e.target.value)}
          >
            <option value="">프리셋 선택</option>
            {presets.map(preset => (
              <option key={preset.id} value={preset.id}>
                {preset.displayName} ({preset.providerType} · {preset.model})
              </option>
            ))}
          </select>
          <Button variant="outline" size="sm" type="button" onClick={onLoadPreset} disabled={!selectedPresetId}>불러오기</Button>
          <Button variant="outline" size="sm" type="button" onClick={onTogglePresetEditor}>현재 설정 저장</Button>
        </div>
        {selectedPreset && (
          <p style={styles.presetMeta}>
            선택됨: {selectedPreset.displayName} · {selectedPreset.providerType} · {selectedPreset.model}
          </p>
        )}
        {presetEditorOpen && (
          <div style={styles.presetEditor}>
            <div style={styles.presetEditorRow}>
              <input
                style={styles.input}
                value={presetName}
                onChange={e => onPresetNameChange(e.target.value)}
                placeholder="프리셋 이름"
              />
              <Button
                size="sm"
                type="button"
                onClick={onCreatePreset}
                disabled={savingPreset || !providerSettingId}
                loading={presetSaveAction === 'create'}
              >
                새 프리셋 저장
              </Button>
            </div>
            <div style={styles.presetEditorActions}>
              <Button
                variant="outline"
                size="sm"
                type="button"
                onClick={onUpdatePreset}
                disabled={!selectedPresetId || savingPreset || !providerSettingId}
                loading={presetSaveAction === 'update'}
              >
                선택 프리셋 업데이트
              </Button>
              <Button variant="outline" size="sm" type="button" onClick={() => onDeletePreset(selectedPreset)} disabled={!selectedPresetId}>
                선택 프리셋 삭제
              </Button>
            </div>
          </div>
        )}
      </div>
    </>
  )
}

function ProviderSettingSelect({ settings, selectedSetting, isCustomSetting, value, onChange, styles }) {
  return (
    <>
      <label style={styles.label}>Provider 프로필</label>
      {settings.length === 0 ? (
        <p style={styles.warn}>등록된 Provider 프로필이 없습니다. Provider 프로필에서 먼저 추가하세요.</p>
      ) : (
        <select style={styles.select} value={value} onChange={e => onChange(e.target.value)}>
          {settings.map(setting => (
            <option key={setting.id} value={setting.id}>
              {setting.displayName} ({setting.providerType} · {setting.model})
            </option>
          ))}
        </select>
      )}

      {selectedSetting && isCustomSetting && (
        <div style={styles.settingInfo}>
          <p style={styles.settingInfoRow}><strong>엔드포인트:</strong> {selectedSetting.endpoint}</p>
          <p style={styles.settingInfoRow}><strong>메서드:</strong> {selectedSetting.method} · <strong>인증:</strong> {selectedSetting.authType}</p>
          {selectedSetting.responsePath && (
            <p style={styles.settingInfoRow}><strong>응답 경로:</strong> {selectedSetting.responsePath}</p>
          )}
        </div>
      )}
    </>
  )
}

function ProviderKeySelect({ keys, filteredKeys, selectedSetting, requiresProviderKey, value, onChange, styles }) {
  return (
    <>
      <label style={styles.label}>API Key</label>
      {!selectedSetting ? (
        <p style={styles.info}>Provider 프로필을 먼저 선택하세요.</p>
      ) : !requiresProviderKey ? (
        <p style={styles.info}>선택한 설정은 인증이 없어 API Key 없이 실행됩니다.</p>
      ) : keys.length === 0 ? (
        <p style={styles.warn}>등록된 API Key가 없습니다. API Key 관리에서 먼저 추가하세요.</p>
      ) : filteredKeys.length === 0 ? (
        <p style={styles.warn}>{selectedSetting?.providerType} 의 API Key가 없습니다. {selectedSetting?.providerType}의 Key를 먼저 추가하세요.</p>
      ) : (
        <select style={styles.select} value={value} onChange={e => onChange(e.target.value)}>
          {filteredKeys.map(key => (
            <option key={key.id} value={key.id}>{key.displayName} ({key.providerType})</option>
          ))}
        </select>
      )}
    </>
  )
}

function PromptFields({ form, onChange, styles }) {
  return (
    <>
      <label style={styles.label}>프롬프트</label>
      <textarea
        style={styles.textarea}
        rows={6}
        placeholder="LLM에 전달할 프롬프트를 입력하세요..."
        value={form.prompt}
        onChange={e => onChange({ prompt: e.target.value })}
        required
      />

      <label style={styles.label}>지시문</label>
      <textarea
        style={styles.textarea}
        rows={3}
        placeholder="응답 스타일이나 제약 조건을 입력하세요."
        value={form.instructions}
        onChange={e => onChange({ instructions: e.target.value })}
      />
    </>
  )
}
