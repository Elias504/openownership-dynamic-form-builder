import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useWorkspaceStore } from '../store/workspaceStore'

interface WorkspaceFormValues {
  name: string
  slug: string
}

export default function WorkspacesPage() {
  const navigate = useNavigate()
  const { workspaces, loading, error, fetchAll, create, remove } = useWorkspaceStore()
  const [showForm, setShowForm] = useState(false)
  const { register, handleSubmit, reset, formState: { errors } } = useForm<WorkspaceFormValues>()

  useEffect(() => { fetchAll() }, [fetchAll])

  const onSubmit = async (values: WorkspaceFormValues) => {
    await create(values)
    reset()
    setShowForm(false)
  }

  return (
    <div style={{ maxWidth: 720, margin: '2rem auto', fontFamily: 'sans-serif' }}>
      <h1>Workspaces</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={() => setShowForm((v) => !v)}>
        {showForm ? 'Cancel' : 'New Workspace'}
      </button>

      {showForm && (
        <form onSubmit={handleSubmit(onSubmit)} style={{ margin: '1rem 0', display: 'flex', gap: '0.5rem' }}>
          <input placeholder="Name" {...register('name', { required: true })} />
          {errors.name && <span>Name is required</span>}
          <input placeholder="Slug" {...register('slug', { required: true, pattern: /^[a-z0-9-]+$/ })} />
          {errors.slug && <span>Slug must be lowercase alphanumeric with hyphens</span>}
          <button type="submit">Create</button>
        </form>
      )}

      {loading && <p>Loading...</p>}

      <ul style={{ listStyle: 'none', padding: 0, marginTop: '1.5rem' }}>
        {workspaces.map((ws) => (
          <li key={ws.id} style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '0.75rem 0', borderBottom: '1px solid #eee' }}>
            <span style={{ flex: 1, cursor: 'pointer', fontWeight: 500 }} onClick={() => navigate(`/workspaces/${ws.id}/forms`)}>
              {ws.name} <small style={{ color: '#666' }}>/{ws.slug}</small>
            </span>
            <button onClick={() => remove(ws.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  )
}