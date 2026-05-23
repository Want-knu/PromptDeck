export default function OptionField({ name, schema, value, onChange, styles }) {
  const commonProps = {
    id: `option-${name}`,
    value: value ?? '',
    onChange: e => onChange(e.target.value)
  }
  const isRangedNumber = (schema.type === 'number' || schema.type === 'integer')
    && schema.minimum != null
    && schema.maximum != null
  const step = schema.type === 'integer' ? 1 : resolveNumberStep(schema)

  return (
    <label style={styles.optionField} htmlFor={`option-${name}`}>
      <span style={styles.optionLabelRow}>
        <span>{name}</span>
        {isRangedNumber && (
          <span style={styles.optionRange}>{schema.minimum} - {schema.maximum}</span>
        )}
      </span>
      {renderInput({ name, schema, value, onChange, commonProps, isRangedNumber, step, styles })}
    </label>
  )
}

function renderInput({ name, schema, value, onChange, commonProps, isRangedNumber, step, styles }) {
  if (schema.enum?.length) {
    return (
      <select style={styles.select} {...commonProps}>
        <option value="">기본값</option>
        {schema.enum.map(option => (
          <option key={option} value={option}>{option}</option>
        ))}
      </select>
    )
  }

  if (schema.type === 'boolean') {
    return (
      <select style={styles.select} {...commonProps}>
        <option value="">기본값</option>
        <option value="true">true</option>
        <option value="false">false</option>
      </select>
    )
  }

  if (isRangedNumber) {
    return (
      <div style={styles.rangeField}>
        <input
          id={`option-${name}`}
          style={styles.range}
          type="range"
          min={schema.minimum}
          max={schema.maximum}
          step={step}
          value={value === '' || value == null ? schema.default ?? schema.minimum : value}
          onChange={e => onChange(e.target.value)}
        />
        <input
          style={styles.rangeNumber}
          type="number"
          min={schema.minimum}
          max={schema.maximum}
          step={step}
          placeholder={schema.default != null ? String(schema.default) : ''}
          {...commonProps}
        />
      </div>
    )
  }

  return (
    <input
      style={styles.input}
      type={schema.type === 'number' || schema.type === 'integer' ? 'number' : 'text'}
      min={schema.minimum}
      max={schema.maximum}
      step={step}
      placeholder={schema.default != null ? String(schema.default) : ''}
      {...commonProps}
    />
  )
}

function resolveNumberStep(schema) {
  return schema.step ?? 0.01
}
