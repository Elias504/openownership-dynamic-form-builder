import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'
import { useSubmissionStore } from '../store/submissionStore'
import { api } from '../api/client'
import type { Field, Form } from '../types'
import Layout from '../components/Layout'

function getOptions(field: Field): string[] {
  const opts = field.config?.options
  return Array.isArray(opts) ? (opts as string[]) : []
}

function formatValue(value: unknown): string {
  if (value === null || value === undefined || value === '') return '—'
  if (typeof value === 'boolean') return value ? 'Yes' : 'No'
  if (Array.isArray(value)) return value.filter(Boolean).join(', ') || '—'
  return String(value)
}

function FieldInput({ field, register, setValue }: {
  field: Field
  register: ReturnType<typeof useForm<Record<string, string>>>['register']
  setValue: (name: string, value: string) => void
}) {
  const options = getOptions(field)

  if (field.type === 'TEXTAREA') {
    return (
      <textarea
        className="form-textarea"
        rows={3}
        {...register(field.id, { required: field.required })}
      />
    )
  }

  if (field.type === 'SELECT') {
    return (
      <select className="form-select" {...register(field.id, { required: field.required })}>
        <option value="">Select an option…</option>
        {options.map((opt) => (
          <option key={opt} value={opt}>{opt}</option>
        ))}
      </select>
    )
  }

  if (field.type === 'RADIO') {
    return (
      <div className="form-options-group">
        {options.map((opt) => (
          <label key={opt} className="form-checkbox-row">
            <input type="radio" value={opt} {...register(field.id, { required: field.required })} />
            {opt}
          </label>
        ))}
      </div>
    )
  }

  if (field.type === 'CHECKBOX') {
    if (options.length > 0) {
      return (
        <div className="form-options-group">
          {options.map((opt) => (
            <label key={opt} className="form-checkbox-row">
              <input type="checkbox" value={opt} {...register(field.id)} />
              {opt}
            </label>
          ))}
        </div>
      )
    }
    return (
      <label className="form-checkbox-row">
        <input type="checkbox" {...register(field.id)} />
        Yes
      </label>
    )
  }

  if (field.type === 'FILE') {
    return (
      <input
        type="file"
        className="form-input"
        style={{ padding: '0.375rem 0.75rem' }}
        onChange={(e) => setValue(field.id, e.target.files?.[0]?.name ?? '')}
      />
    )
  }

  return (
    <input
      className="form-input"
      type={field.type === 'NUMBER' ? 'number' : field.type === 'DATE' ? 'date' : 'text'}
      {...register(field.id, { required: field.required })}
    />
  )
}

export default function SubmissionsPage() {
  const { formId } = useParams<{ formId: string }>()
  const navigate = useNavigate()
  const { fields, fetchFields } = useFormStore()
  const { submissions, loading, error, fetchByForm, submit } = useSubmissionStore()
  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm<Record<string, string>>()
  const [form, setForm] = useState<Form | null>(null)

  useEffect(() => {
    if (!formId) return
    void fetchFields(formId)
    void fetchByForm(formId)
    api.forms.get(formId).then(setForm).catch(() => {})
  }, [formId, fetchFields, fetchByForm])

  const fieldMap = Object.fromEntries(fields.map((f) => [f.id, f]))

  const requiredFields = fields.filter((f) => f.required)
  const missingCount = requiredFields.filter((f) => errors[f.id]).length

  const onSubmit = async (values: Record<string, string>) => {
    if (!formId) return
    await submit(formId, values as Record<string, unknown>)
    reset()
  }

  return (
    <Layout
      title={form ? `${form.title} — Submissions` : 'Submissions'}
      breadcrumbs={[
        { label: 'Workspaces', to: '/' },
        { label: 'Forms', onClick: () => navigate(-1) },
      ]}
    >
      {error && <div className="error-banner">{error}</div>}

      {fields.length > 0 && (
        <section className="submit-form-section">
          <p className="submit-form-section__title">Submit a response</p>

          {missingCount > 0 && (
            <div className="validation-banner">
              Please fill in{' '}
              {missingCount === 1
                ? 'the required field marked with *'
                : `all ${missingCount} required fields marked with *`}{' '}
              before submitting.
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="submit-form-fields">
              {fields.map((field) => (
                <div key={field.id} className="form-group">
                  <label className="form-label">
                    {field.label}
                    {field.required && <span className="form-required"> *</span>}
                  </label>
                  <FieldInput field={field} register={register} setValue={setValue} />
                </div>
              ))}
            </div>
            <div style={{ marginTop: '1.25rem' }}>
              <button type="submit" className="btn">Submit response</button>
            </div>
          </form>
        </section>
      )}

      <div className="section-heading">
        <span>Responses</span>
        <span className="section-count">{submissions.length}</span>
      </div>

      {loading && <p className="loading-text">Loading submissions…</p>}

      {!loading && submissions.length === 0 && (
        <div className="empty-state">
          <p className="empty-state__title">No responses yet</p>
          <p className="empty-state__text">Submit the form above to record the first response.</p>
        </div>
      )}

      <div>
        {submissions.map((sub) => (
          <details key={sub.id} className="submission-row">
            <summary>
              <span className="submission-row__date">
                {new Date(sub.createdAt).toLocaleString(undefined, {
                  dateStyle: 'medium',
                  timeStyle: 'short',
                })}
              </span>
              <span className="submission-row__chevron">›</span>
            </summary>
            <div className="submission-data">
              {Object.entries(sub.data).map(([key, value]) => (
                <div key={key} className="submission-data__row">
                  <span className="submission-data__label">
                    {fieldMap[key]?.label ?? key}
                  </span>
                  <span className="submission-data__value">
                    {formatValue(value)}
                  </span>
                </div>
              ))}
            </div>
          </details>
        ))}
      </div>
    </Layout>
  )
}