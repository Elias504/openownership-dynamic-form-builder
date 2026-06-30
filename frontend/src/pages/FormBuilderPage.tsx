import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'
import type { FieldType } from '../types'

const FIELD_TYPES: FieldType[] = ['TEXT', 'TEXTAREA', 'NUMBER', 'DATE', 'SELECT', 'CHECKBOX', 'RADIO', 'FILE']

interface FieldFormValues {
  label: string
  type: FieldType
  required: boolean
}

export default function FormBuilderPage() {
  const { formId } = useParams<{ formId: string }>()
  const navigate = useNavigate()
  const { fields, loading, error, fetchFields, addField, removeField } = useFormStore()
  const [showFieldForm, setShowFieldForm] = useState(false)
  const { register, handleSubmit, reset } = useForm<FieldFormValues>({ defaultValues: { type: 'TEXT', required: false } })

  useEffect(() => {
    if (formId) fetchFields(formId)
  }, [formId, fetchFields])

  const onAddField = async (values: FieldFormValues) => {
    if (!formId) return
    await addField(formId, { ...values, displayOrder: fields.length })
    reset({ type: 'TEXT', required: false })
    setShowFieldForm(false)
  }

  return (
    <div style={{ maxWidth: 720, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <button onClick={() => navigate(-1)}>← Back</button>
      <h1>Form Builder</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={() => setShowFieldForm((v) => !v)}>
        {showFieldForm ? 'Cancel' : 'Add Field'}
      </button>

      {showFieldForm && (
        <form onSubmit={handleSubmit(onAddField)} style={{ margin: '1rem 0', display: 'flex', gap: '0.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
          <input placeholder="Label" {...register('label', { required: true })} />
          <select {...register('type')}>
            {FIELD_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
          </select>
          <label style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            <input type="checkbox" {...register('required')} /> Required
          </label>
          <button type="submit">Add</button>
        </form>
      )}

      {loading && <p>Loading...</p>}

      <div style={{ marginTop: '1.5rem' }}>
        {fields.length === 0 && !loading && <p style={{ color: '#999' }}>No fields yet. Add one above.</p>}
        {fields.map((field, idx) => (
          <div key={field.id} style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem', border: '1px solid #ddd', borderRadius: 4, marginBottom: 8 }}>
            <span style={{ width: 24, color: '#999', textAlign: 'center' }}>{idx + 1}</span>
            <span style={{ flex: 1 }}>
              <strong>{field.label}</strong>
              <small style={{ marginLeft: '0.5rem', color: '#666' }}>{field.type}</small>
              {field.required && <small style={{ marginLeft: '0.5rem', color: '#c00' }}>*required</small>}
            </span>
            <button onClick={() => removeField(field.id)}>Remove</button>
          </div>
        ))}
      </div>
    </div>
  )
}