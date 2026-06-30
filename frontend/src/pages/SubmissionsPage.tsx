import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'
import { useSubmissionStore } from '../store/submissionStore'
import { api } from '../api/client'
import type { Form } from '../types'
import Layout from '../components/Layout'

export default function SubmissionsPage() {
  const { formId } = useParams<{ formId: string }>()
  const navigate = useNavigate()
  const { fields, fetchFields } = useFormStore()
  const { submissions, loading, error, fetchByForm, submit } = useSubmissionStore()
  const { register, handleSubmit, reset } = useForm<Record<string, string>>()
  const [form, setForm] = useState<Form | null>(null)

  useEffect(() => {
    if (!formId) return
    fetchFields(formId)
    fetchByForm(formId)
    api.forms.get(formId).then(setForm).catch(() => {})
  }, [formId, fetchFields, fetchByForm])

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
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="submit-form-fields">
              {fields.map((field) => (
                <div key={field.id} className="form-group">
                  <label className="form-label">
                    {field.label}
                    {field.required && <span className="form-required"> *</span>}
                  </label>
                  {field.type === 'TEXTAREA' ? (
                    <textarea
                      className="form-textarea"
                      rows={3}
                      {...register(field.id, { required: field.required })}
                    />
                  ) : field.type === 'CHECKBOX' ? (
                    <label className="form-checkbox-row">
                      <input type="checkbox" {...register(field.id)} />
                      Yes
                    </label>
                  ) : (
                    <input
                      className="form-input"
                      type={
                        field.type === 'NUMBER' ? 'number'
                        : field.type === 'DATE' ? 'date'
                        : 'text'
                      }
                      {...register(field.id, { required: field.required })}
                    />
                  )}
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
            <div className="submission-row__body">
              {JSON.stringify(sub.data, null, 2)}
            </div>
          </details>
        ))}
      </div>
    </Layout>
  )
}