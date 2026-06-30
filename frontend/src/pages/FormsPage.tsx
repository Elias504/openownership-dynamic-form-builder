import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'

interface FormValues {
  title: string
  description: string
}

export default function FormsPage() {
  const { workspaceId } = useParams<{ workspaceId: string }>()
  const navigate = useNavigate()
  const { forms, loading, error, fetchForms, createForm, removeForm } = useFormStore()
  const [showForm, setShowForm] = useState(false)
  const { register, handleSubmit, reset } = useForm<FormValues>()

  useEffect(() => {
    if (workspaceId) fetchForms(workspaceId)
  }, [workspaceId, fetchForms])

  const onSubmit = async (values: FormValues) => {
    if (!workspaceId) return
    await createForm(workspaceId, { title: values.title, description: values.description || undefined })
    reset()
    setShowForm(false)
  }

  return (
    <div style={{ maxWidth: 720, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <button onClick={() => navigate('/')}>← Workspaces</button>
      <h1>Forms</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={() => setShowForm((v) => !v)}>
        {showForm ? 'Cancel' : 'New Form'}
      </button>

      {showForm && (
        <form onSubmit={handleSubmit(onSubmit)} style={{ margin: '1rem 0', display: 'flex', flexDirection: 'column', gap: '0.5rem', maxWidth: 400 }}>
          <input placeholder="Title" {...register('title', { required: true })} />
          <input placeholder="Description (optional)" {...register('description')} />
          <button type="submit">Create</button>
        </form>
      )}

      {loading && <p>Loading...</p>}

      <ul style={{ listStyle: 'none', padding: 0, marginTop: '1.5rem' }}>
        {forms.map((form) => (
          <li key={form.id} style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 0', borderBottom: '1px solid #eee' }}>
            <span style={{ flex: 1 }}>
              <strong>{form.title}</strong>
              {form.description && <small style={{ marginLeft: '0.5rem', color: '#666' }}>{form.description}</small>}
              {form.published && <span style={{ marginLeft: '0.5rem', color: 'green', fontSize: 12 }}>Published</span>}
            </span>
            <button onClick={() => navigate(`/forms/${form.id}/builder`)}>Builder</button>
            <button onClick={() => navigate(`/forms/${form.id}/submissions`)}>Submissions</button>
            <button onClick={() => removeForm(form.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  )
}