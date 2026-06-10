export const EMPTY_PROVIDER_KEY_FORM = {
  providerType: 'OPENAI',
  apiKey: '',
  displayName: ''
}

export function createProviderKeyEditForm(providerKey) {
  return {
    providerType: providerKey.providerType,
    apiKey: '',
    displayName: providerKey.displayName
  }
}

export function buildProviderKeyUpdatePayload(providerKey, form) {
  return {
    version: providerKey.version,
    providerType: form.providerType,
    apiKey: form.apiKey,
    displayName: form.displayName
  }
}
