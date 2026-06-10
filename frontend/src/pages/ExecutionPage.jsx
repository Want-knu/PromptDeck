import { useState, useEffect, useMemo, useRef } from 'react'
import Navbar from '../components/Navbar'
import { Button, LoadingSpinner, Modal } from '../components/ui'
import ExecutionConfigPanel from '../components/execution/ExecutionConfigPanel'
import ExecutionHero from '../components/execution/ExecutionHero'
import ExecutionResultPanel from '../components/execution/ExecutionResultPanel'
import { getProviderKeys } from '../api/providers'
import { getProviderSettings, getProviderSettingOptions } from '../api/providerSettings'
import {
  createExecutionPreset,
  deleteExecutionPreset,
  executeProvider,
  getExecutionPresets,
  previewProvider,
  updateExecutionPreset
} from '../api/executions'
import {
  buildExecutionPayload,
  defaultOptionValues,
  parseOptionSchema,
  resolveOptionSchemaJson,
  stringifyOptionValues
} from '../utils/executionOptions'
import { executionPageStyles as st } from '../styles/pageStyles/executionPageStyles'

export default function ExecutionPage() {
  const [keys, setKeys] = useState([])
  const [settings, setSettings] = useState([])
  const [presets, setPresets] = useState([])
  const [settingOptions, setSettingOptions] = useState(null)
  const [loadingDeps, setLoadingDeps] = useState(true)

  const [form, setForm] = useState({
    providerKeyId: '',
    providerSettingId: '',
    prompt: '',
    instructions: '',
    variables: ''
  })
  const [selectedPresetId, setSelectedPresetId] = useState('')
  const [presetName, setPresetName] = useState('')
  const [presetEditorOpen, setPresetEditorOpen] = useState(false)
  const [advancedOpen, setAdvancedOpen] = useState(false)
  const [optionValues, setOptionValues] = useState({})
  const applyingPresetRef = useRef(false)

  const [preview, setPreview] = useState(null)
  const [result, setResult] = useState(null)
  const [previewing, setPreviewing] = useState(false)
  const [executing, setExecuting] = useState(false)
  const [savingPreset, setSavingPreset] = useState(false)
  const [presetSaveAction, setPresetSaveAction] = useState('')
  const [deletePresetTarget, setDeletePresetTarget] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([getProviderKeys(), getProviderSettings(), getProviderSettingOptions(), getExecutionPresets()])
      .then(([k, ps, options, presetData]) => {
        setKeys(k ?? [])
        setSettings(ps ?? [])
        setSettingOptions(options)
        setPresets(presetData ?? [])
        if (ps?.length) setForm(p => ({ ...p, providerSettingId: String(ps[0].id) }))
      })
      .catch(err => setError(err.message))
      .finally(() => setLoadingDeps(false))
  }, [])

  const selectedSetting = settings.find(s => String(s.id) === form.providerSettingId)
  const selectedPreset = presets.find(p => String(p.id) === selectedPresetId)
  const isCustomSetting = selectedSetting?.providerType === 'CUSTOM'
  const requiresProviderKey = selectedSetting?.authType !== 'NONE'
  const filteredKeys = useMemo(() => (
    selectedSetting
      ? keys.filter(k => k.providerType === selectedSetting.providerType)
      : []
  ), [keys, selectedSetting])
  const optionSchema = parseOptionSchema(resolveOptionSchemaJson(selectedSetting, settingOptions))
  const canRun = Boolean(
    form.prompt.trim()
      && form.providerSettingId
      && (!requiresProviderKey || form.providerKeyId)
  )

  useEffect(() => {
    if (!selectedSetting) {
      if (form.providerKeyId) {
        setForm(p => ({ ...p, providerKeyId: '' }))
      }
      return
    }

    if (!requiresProviderKey) {
      if (form.providerKeyId) {
        setForm(p => ({ ...p, providerKeyId: '' }))
      }
      return
    }

    const selectedKeyMatches = filteredKeys.some(k => String(k.id) === form.providerKeyId)
    if (!selectedKeyMatches) {
      setForm(p => ({ ...p, providerKeyId: filteredKeys[0] ? String(filteredKeys[0].id) : '' }))
    }
  }, [selectedSetting, requiresProviderKey, filteredKeys, form.providerKeyId])

  useEffect(() => {
    if (applyingPresetRef.current) {
      applyingPresetRef.current = false
      return
    }
    setOptionValues(defaultOptionValues(optionSchema))
  }, [form.providerSettingId, selectedSetting?.model, selectedSetting?.optionSchemaJson, settingOptions])

  function applyPreset(preset) {
    const setting = settings.find(s => s.id === preset.providerSettingId)
    if (!setting) {
      setError('프리셋의 Provider 프로필을 찾을 수 없습니다.')
      return
    }

    const presetSchema = parseOptionSchema(resolveOptionSchemaJson(setting, settingOptions))
    const defaults = defaultOptionValues(presetSchema)
    applyingPresetRef.current = true
    setForm(p => ({
      ...p,
      providerSettingId: String(preset.providerSettingId),
      providerKeyId: preset.providerKeyId ? String(preset.providerKeyId) : p.providerKeyId,
      prompt: preset.prompt ?? '',
      instructions: preset.instructions ?? '',
      variables: preset.variables && Object.keys(preset.variables).length
        ? JSON.stringify(preset.variables, null, 2)
        : ''
    }))
    setOptionValues({
      ...defaults,
      ...stringifyOptionValues(preset.options ?? {})
    })
    setPreview(null)
    setResult(null)
    setError('')
  }

  async function handleLoadPreset() {
    if (selectedPreset) {
      applyPreset(selectedPreset)
      setPresetName(selectedPreset.displayName)
      setPresetEditorOpen(false)
    }
  }

  function buildPresetPayload(displayName) {
    const payload = buildPayload()
    return {
      displayName,
      providerSettingId: payload.providerSettingId,
      providerKeyId: payload.providerKeyId,
      prompt: payload.prompt,
      instructions: payload.instructions,
      options: payload.options,
      variables: payload.variables
    }
  }

  async function handleCreatePreset() {
    setError('')
    if (!form.providerSettingId) {
      setError('Provider 프로필을 먼저 선택하세요.')
      return
    }
    if (!presetName.trim()) {
      setError('프리셋 이름을 입력하세요.')
      return
    }

    setSavingPreset(true)
    setPresetSaveAction('create')
    try {
      const saved = await createExecutionPreset(buildPresetPayload(presetName.trim()))
      setPresets(prev => [saved, ...prev])
      setSelectedPresetId(String(saved.id))
      setPresetName(saved.displayName)
      setPresetEditorOpen(false)
    } catch (err) {
      setError(err.message)
    } finally {
      setSavingPreset(false)
      setPresetSaveAction('')
    }
  }

  async function handleUpdatePreset() {
    setError('')
    if (!selectedPreset) {
      setError('수정할 프리셋을 먼저 선택하세요.')
      return
    }
    if (!presetName.trim()) {
      setError('프리셋 이름을 입력하세요.')
      return
    }

    setSavingPreset(true)
    setPresetSaveAction('update')
    try {
      const saved = await updateExecutionPreset(selectedPreset.id, buildPresetPayload(presetName.trim()))
      setPresets(prev => prev.map(p => p.id === saved.id ? saved : p))
      setSelectedPresetId(String(saved.id))
      setPresetName(saved.displayName)
      setPresetEditorOpen(false)
    } catch (err) {
      setError(err.message)
    } finally {
      setSavingPreset(false)
      setPresetSaveAction('')
    }
  }

  async function handleDeletePreset() {
    const preset = deletePresetTarget ?? presets.find(p => String(p.id) === selectedPresetId)
    if (!preset) {
      return
    }

    try {
      await deleteExecutionPreset(preset.id)
      setPresets(prev => prev.filter(p => p.id !== preset.id))
      setSelectedPresetId('')
      setPresetName('')
      setPresetEditorOpen(false)
      setDeletePresetTarget(null)
    } catch (err) {
      setError(err.message)
    }
  }

  function buildPayload() {
    return buildExecutionPayload({ form, optionSchema, optionValues, requiresProviderKey })
  }

  function updateForm(patch) {
    setForm(prev => ({ ...prev, ...patch }))
  }

  function handlePresetSelect(id) {
    const preset = presets.find(p => String(p.id) === id)
    setSelectedPresetId(id)
    setPresetName(preset?.displayName ?? '')
  }

  function updateOptionValue(name, value) {
    setOptionValues(prev => ({ ...prev, [name]: value }))
  }

  async function handlePreview() {
    setError('')
    setPreview(null)
    setResult(null)
    if (!form.prompt.trim()) {
      setError('프롬프트를 입력하세요.')
      return
    }
    setPreviewing(true)
    try {
      const payload = buildPayload()
      const data = await previewProvider(payload)
      setPreview(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setPreviewing(false)
    }
  }

  async function handleExecute() {
    setError('')
    setResult(null)
    setPreview(null)
    if (!form.prompt.trim()) {
      setError('프롬프트를 입력하세요.')
      return
    }
    setExecuting(true)
    try {
      const payload = buildPayload()
      const data = await executeProvider(payload)
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setExecuting(false)
    }
  }

  return (
    <>
      <Navbar />
      <main className="pd-page-enter" style={st.main}>
        <ExecutionHero
          settingsCount={settings.length}
          presetsCount={presets.length}
          keysCount={keys.length}
          styles={st}
        />

        {loadingDeps ? (
          <LoadingSpinner />
        ) : (
          <div style={st.layout}>
            <ExecutionConfigPanel
              form={form}
              settings={settings}
              keys={keys}
              presets={presets}
              selectedSetting={selectedSetting}
              selectedPreset={selectedPreset}
              selectedPresetId={selectedPresetId}
              presetName={presetName}
              presetEditorOpen={presetEditorOpen}
              advancedOpen={advancedOpen}
              optionSchema={optionSchema}
              optionValues={optionValues}
              filteredKeys={filteredKeys}
              isCustomSetting={isCustomSetting}
              requiresProviderKey={requiresProviderKey}
              savingPreset={savingPreset}
              presetSaveAction={presetSaveAction}
              previewing={previewing}
              executing={executing}
              canRun={canRun}
              error={error}
              onFormChange={updateForm}
              onOptionChange={updateOptionValue}
              onPresetSelect={handlePresetSelect}
              onPresetNameChange={setPresetName}
              onTogglePresetEditor={() => setPresetEditorOpen(open => !open)}
              onLoadPreset={handleLoadPreset}
              onCreatePreset={handleCreatePreset}
              onUpdatePreset={handleUpdatePreset}
              onDeletePreset={setDeletePresetTarget}
              onToggleAdvanced={() => setAdvancedOpen(open => !open)}
              onPreview={handlePreview}
              onExecute={handleExecute}
              styles={st}
            />

            <ExecutionResultPanel preview={preview} result={result} styles={st} />
          </div>
        )}
        <Modal
          isOpen={Boolean(deletePresetTarget)}
          onClose={() => setDeletePresetTarget(null)}
          title="실행 프리셋 삭제"
          width="420px"
          footer={(
            <>
              <Button variant="outline" onClick={() => setDeletePresetTarget(null)}>취소</Button>
              <Button variant="danger" onClick={handleDeletePreset}>삭제</Button>
            </>
          )}
        >
          <p style={st.confirmText}>
            {deletePresetTarget ? `"${deletePresetTarget.displayName}" 프리셋을 삭제합니다.` : ''}
          </p>
        </Modal>
      </main>
    </>
  )
}
