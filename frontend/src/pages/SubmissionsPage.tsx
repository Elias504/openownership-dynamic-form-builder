import { useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'
import { useSubmissionStore } from '../store/submissionStore'

export default function SubmissionsPage() {
  const { formId } = useParams<{ formId: string }>()
  const navigate = useNavigate()
  const { fields, fetchFields } = useFormStore()
  const { submissions, loading, error, fetchByForm, submit } = useSubmissionStore()
  const { register, handleSubmit, reset } = useForm<Record<string, string>>()

  useEffect(() => {
    if (!formId) return
    fetchFields(formId)
    fetchByForm(formId)
  }, [formId, fetchFields, fetchByForm])

  const onSubmit = async (values: Record<string, string>) => {
    if (!formId) return
    await submit(formId, values as Record<string, unknown>)
    reset()
  }

  return (
    <div style={{ maxWidth: 720, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <button onClick={() => navigate(-1)}>← Back</button>
      <h1>Submissions</h1>

      {fields.length > 0 && (
        <section style={{ marginBottom: '2rem', padding: '1rem', border: '1px solid #ddd', borderRadius: 4 }}>
          <h2>Submit a response</h2>
          <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
            {fields.map((field) => (
              <label key={field.id} style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                <span>{field.label}{field.required && <span style={{ color: 'red' }}> *</span>}</span>
                {field.type === 'TEXTAREA' ? (
                  <textarea {...register(field.id, { required: field.required })} rows={3} />
                ) : field.type === 'CHECKBOX' ? (
                  <input type="checkbox" {...register(field.id)} />
                ) : (
                  <input
                    type={field.type === 'NUMBER' ? 'number' : field.type === 'DATE' ? 'date' : 'text'}
                    {...register(field.id, { required: field.required })}
                  />
                )}
              </label>
            ))}
            <button type="submit" style={{ alignSelf: 'flex-start' }}>Submit</button>
          </form>
        </section>
      )}

      {error && <p style={{ color: 'red' }}>{error}</p>}
      {loading && <p>Loading...</p>}

      <h2>Responses ({submissions.length})</h2>
      {submissions.length === 0 && !loading && <p style={{ color: '#999' }}>No submissions yet.</p>}
      {submissions.map((sub) => (
        <details key={sub.id} style={{ marginBottom: 8, padding: '0.75rem', border: '1px solid #eee', borderRadius: 4 }}>
          <summary style={{ cursor: 'pointer' }}>
            {new Date(sub.createdAt).toLocaleString()}
          </summary>
          <pre style={{ marginTop: '0.5rem', fontSize: 12, background: '#f5f5f5', padding: '0.5rem', borderRadius: 4 }}>
            {JSON.stringify(sub.data, null, 2)}
          </pre>
        </details>
      ))}
    </div>
  )
}