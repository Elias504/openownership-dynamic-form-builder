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
  TEXTAREA: 'Text area',
  NUMBER: 'Number',
  DATE: 'Date',
  SELECT: 'Dropdown',
  CHECKBOX: 'Checkbox',
  RADIO: 'Radio',
  FILE: 'File',
}

const TYPES_WITH_OPTIONS: FieldType[] = ['SELECT', 'CHECKBOX', 'RADIO']

interface FieldFormValues {
  label: string
  type: FieldType
  required: boolean
  options: string
}

export default function FormBuilderPage() {
  const { formId } = useParams<{ formId: string }>()
  const navigate = useNavigate()
  const { fields, loading, error, fetchFields, addField, removeField } = useFormStore()
  const [showFieldForm, setShowFieldForm] = useState(false)
  const [form, setForm] = useState<Form | null>(null)
  const { register, handleSubmit, reset, watch } = useForm<FieldFormValues>({
    defaultValues: { type: 'TEXT', required: false, options: '' },
  })

  const watchedType = watch('type')
  const needsOptions = TYPES_WITH_OPTIONS.includes(watchedType)

  useEffect(() => {
    if (!formId) return
    void fetchFields(formId)
    api.forms.get(formId).then(setForm).catch(() => {})
  }, [formId, fetchFields])

  const onAddField = async (values: FieldFormValues) => {
    if (!formId) return
    const config: Record<string, unknown> = {}
    if (needsOptions && values.options.trim()) {
      config.options = values.options.split('\n').map((s) => s.trim()).filter(Boolean)
    }
    await addField(formId, { label: values.label, type: values.type, required: values.required, displayOrder: fields.length, config })
    reset({ type: 'TEXT', required: false, options: '' })
    setShowFieldForm(false)
  }

  const defaultValues = { type: 'TEXT' as FieldType, required: false, options: '' }

  return (
    <Layout
      title={form?.title ?? 'Form Builder'}
      breadcrumbs={[
        { label: 'Workspaces', to: '/' },
        { label: 'Forms', onClick: () => navigate(-1) },
      ]}
      action={
        <button
          className={showFieldForm ? 'btn btn-cancel btn-sm' : 'btn btn-sm'}
          onClick={() => { setShowFieldForm((v) => !v); reset(defaultValues) }}
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
            <div className="form-panel__grid form-panel__grid-3">
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
            </div>

            {needsOptions && (
              <div className="form-group" style={{ marginTop: '1rem' }}>
                <label className="form-label">
                  Options <span className="text-muted text-sm" style={{ fontWeight: 400 }}> — one per line</span>
                </label>
                <textarea
                  className="form-textarea"
                  rows={4}
                  placeholder={`Option A\nOption B\nOption C`}
                  {...register('options')}
                />
              </div>
            )}

            <div className="form-panel__actions">
              <button type="submit" className="btn">Add Field</button>
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
            {fields.map((field, idx) => {
              const opts = field.config?.options as string[] | undefined
              return (
                <div key={field.id} className="field-row">
                  <span className="field-row__num">{String(idx + 1).padStart(2, '0')}</span>
                  <div className="field-row__label">
                    {field.label}
                    {opts && opts.length > 0 && (
                      <span className="text-muted text-sm" style={{ marginLeft: '0.5rem', fontWeight: 400 }}>
                        ({opts.length} option{opts.length !== 1 ? 's' : ''})
                      </span>
                    )}
                  </div>
                  <div className="field-row__meta">
                    <span className="badge badge-gray">{FIELD_TYPE_LABELS[field.type]}</span>
                    {field.required && <span className="badge badge-outline">Required</span>}
                    <button className="btn-ghost" onClick={() => removeField(field.id)}>Remove</button>
                  </div>
                </div>
              )
            })}
          </div>
        </div>
      )}

      <div className="builder-page-nav">
        <button className="btn btn-cancel btn-sm" onClick={() => navigate(-1)}>
          ← back to Forms
        </button>
        <button
          className="btn btn-outline btn-sm"
          onClick={() => navigate(`/forms/${formId}/submissions`)}
        >
          to Submissions →
        </button>
      </div>
    </Layout>
  )
}