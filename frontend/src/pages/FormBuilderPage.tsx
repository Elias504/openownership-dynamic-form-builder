import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'
import { api } from '../api/client'
import type { FieldType, Form } from '../types'
import Layout from '../components/Layout'

const FIELD_TYPES: FieldType[] = ['TEXT', 'TEXTAREA', 'NUMBER', 'DATE', 'SELECT', 'CHECKBOX', 'RADIO', 'FILE']

const FIELD_TYPE_LABELS: Record<FieldType, string> = {
  TEXT: 'Text',
  TEXTAREA: 'Long text',
  NUMBER: 'Number',
  DATE: 'Date',
  SELECT: 'Dropdown',
  CHECKBOX: 'Checkbox',
  RADIO: 'Radio',
  FILE: 'File',
}

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
  const [form, setForm] = useState<Form | null>(null)
  const { register, handleSubmit, reset } = useForm<FieldFormValues>({
    defaultValues: { type: 'TEXT', required: false },
  })

  useEffect(() => {
    if (!formId) return
    fetchFields(formId)
    api.forms.get(formId).then(setForm).catch(() => {})
  }, [formId, fetchFields])

  const onAddField = async (values: FieldFormValues) => {
    if (!formId) return
    await addField(formId, { ...values, displayOrder: fields.length })
    reset({ type: 'TEXT', required: false })
    setShowFieldForm(false)
  }

  return (
    <Layout
      title={form?.title ?? 'Form Builder'}
      breadcrumbs={[
        { label: 'Workspaces', to: '/' },
        { label: 'Forms', onClick: () => navigate(-1) },
      ]}
      action={
        <button
          className={showFieldForm ? 'btn btn-cancel' : 'btn'}
          onClick={() => { setShowFieldForm((v) => !v); reset({ type: 'TEXT', required: false }) }}
        >
          {showFieldForm ? 'Cancel' : '+ Add Field'}
        </button>
      }
    >
      {error && <div className="error-banner">{error}</div>}

      {showFieldForm && (
        <div className="form-panel">
          <p className="form-panel__title">New Field</p>
          <form onSubmit={handleSubmit(onAddField)}>
            <div className="form-panel__grid form-panel__grid-4">
              <div className="form-group">
                <label className="form-label">Label <span className="form-required">*</span></label>
                <input
                  className="form-input"
                  placeholder="e.g. Full name"
                  {...register('label', { required: true })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Type</label>
                <select className="form-select" {...register('type')}>
                  {FIELD_TYPES.map((t) => (
                    <option key={t} value={t}>{FIELD_TYPE_LABELS[t]}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">&nbsp;</label>
                <label className="form-checkbox-row">
                  <input type="checkbox" {...register('required')} />
                  Required
                </label>
              </div>
              <div className="form-group">
                <label className="form-label">&nbsp;</label>
                <button type="submit" className="btn">Add Field</button>
              </div>
            </div>
          </form>
        </div>
      )}

      {loading && <p className="loading-text">Loading fields…</p>}

      {!loading && fields.length === 0 && !showFieldForm && (
        <div className="empty-state">
          <p className="empty-state__title">No fields yet</p>
          <p className="empty-state__text">Add fields to define what information this form collects.</p>
        </div>
      )}

      {fields.length > 0 && (
        <div>
          <div className="section-heading">
            <span>Fields</span>
            <span className="section-count">{fields.length} field{fields.length !== 1 ? 's' : ''}</span>
          </div>
          <div>
            {fields.map((field, idx) => (
              <div key={field.id} className="field-row">
                <span className="field-row__num">{String(idx + 1).padStart(2, '0')}</span>
                <span className="field-row__label">{field.label}</span>
                <div className="field-row__meta">
                  <span className="badge badge-gray">{FIELD_TYPE_LABELS[field.type]}</span>
                  {field.required && (
                    <span className="badge badge-outline">Required</span>
                  )}
                  <button className="btn-ghost" onClick={() => removeField(field.id)}>
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </Layout>
  )
}