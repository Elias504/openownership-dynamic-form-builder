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

function FieldInput({
  field,
  register,
  onFileChange,
}: {
  field: Field
  register: ReturnType<typeof useForm<Record<string, string>>>['register']
  onFileChange: (fieldId: string, file: File | null) => void
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
        onChange={(e) => onFileChange(field.id, e.target.files?.[0] ?? null)}
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
  const { register, handleSubmit, reset, formState: { errors } } = useForm<Record<string, string>>()
  const [form, setForm] = useState<Form | null>(null)
  const [fileSelections, setFileSelections] = useState<Record<string, File>>({})

  useEffect(() => {
    if (!formId) return
    void fetchFields(formId)
    void fetchByForm(formId)
    api.forms.get(formId).then(setForm).catch(() => {})
  }, [formId, fetchFields, fetchByForm])

  const fieldMap = Object.fromEntries(fields.map((f) => [f.id, f]))

  const requiredFields = fields.filter((f) => f.required)
  const missingCount = requiredFields.filter((f) => errors[f.id]).length

  const handleFileChange = (fieldId: string, file: File | null) => {
    setFileSelections((prev) => {
      if (file) return { ...prev, [fieldId]: file }
      const next = { ...prev }
      delete next[fieldId]
      return next
    })
  }

  const onSubmit = async (values: Record<string, string>) => {
    if (!formId) return
    const data: Record<string, unknown> = { ...values }

    for (const field of fields) {
      if (field.type === 'FILE') {
        const file = fileSelections[field.id]
        if (file) {
          data[field.id] = await api.files.upload(file)
        }
      }
    }

    await submit(formId, data)
    reset()
    setFileSelections({})
  }

  return (
    <Layout
      title={form ? `${form.title} — Submissions` : 'Submissions'}
      breadcrumbs={[
        { label: 'Workspaces', to: '/' },
        { label: 'Forms', onClick: () => navigate(-1) },
      ]}
      action={
        <button className="btn btn-cancel btn-sm" onClick={() => navigate(-1)}>
          ← back to Forms
        </button>
      }
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
                  <FieldInput field={field} register={register} onFileChange={handleFileChange} />
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
              {Object.entries(sub.data).map(([key, value]) => {
                const field = fieldMap[key]
                const isFileKey = field?.type === 'FILE' && typeof value === 'string' && value !== ''
                return (
                  <div key={key} className="submission-data__row">
                    <span className="submission-data__label">
                      {field?.label ?? key}
                    </span>
                    {isFileKey ? (
                      <a
                        href={api.files.downloadUrl(value as string)}
                        className="btn-download-icon"
                        title="Download file"
                        download
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                          <polyline points="7 10 12 15 17 10"/>
                          <line x1="12" y1="15" x2="12" y2="3"/>
                        </svg>
                      </a>
                    ) : (
                      <span className="submission-data__value">
                        {formatValue(value)}
                      </span>
                    )}
                  </div>
                )
              })}
            </div>
          </details>
        ))}
      </div>
    </Layout>
  )
}