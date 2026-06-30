import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useWorkspaceStore } from '../store/workspaceStore'
import Layout from '../components/Layout'

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
    <Layout
      title="Workspaces"
      action={
        <button
          className={showForm ? 'btn btn-cancel' : 'btn'}
          onClick={() => { setShowForm((v) => !v); reset() }}
        >
          {showForm ? 'Cancel' : '+ New Workspace'}
        </button>
      }
    >
      {error && <div className="error-banner">{error}</div>}

      {showForm && (
        <div className="form-panel">
          <p className="form-panel__title">New Workspace</p>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="form-panel__grid form-panel__grid-2">
              <div className="form-group">
                <label className="form-label">
                  Name <span className="form-required">*</span>
                </label>
                <input
                  className="form-input"
                  placeholder="e.g. Anti-Corruption Unit"
                  {...register('name', { required: 'Name is required' })}
                />
                {errors.name && <span className="form-error">{errors.name.message}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">
                  Slug <span className="form-required">*</span>
                </label>
                <input
                  className="form-input"
                  placeholder="e.g. anti-corruption"
                  {...register('slug', {
                    required: 'Slug is required',
                    pattern: { value: /^[a-z0-9-]+$/, message: 'Lowercase letters, numbers, hyphens only' },
                  })}
                />
                {errors.slug && <span className="form-error">{errors.slug.message}</span>}
              </div>
            </div>
            <div className="form-panel__actions">
              <button type="submit" className="btn">Create Workspace</button>
            </div>
          </form>
        </div>
      )}

      {loading && <p className="loading-text">Loading workspaces…</p>}

      {!loading && workspaces.length === 0 && !showForm && (
        <div className="empty-state">
          <p className="empty-state__title">No workspaces yet</p>
          <p className="empty-state__text">Create a workspace to organise your forms.</p>
        </div>
      )}

      <div className="card-grid">
        {workspaces.map((ws) => (
          <div key={ws.id} className="card">
            <div className="flex items-center justify-between gap-4">
              <div
                className="flex-1"
                style={{ cursor: 'pointer' }}
                onClick={() => navigate(`/workspaces/${ws.id}/forms`)}
              >
                <p className="card-title">{ws.name}</p>
                <span className="badge badge-gray mt-2" style={{ marginTop: '0.375rem', display: 'inline-block' }}>
                  /{ws.slug}
                </span>
              </div>
              <div className="flex gap-3 items-center">
                <button className="btn btn-sm" onClick={() => navigate(`/workspaces/${ws.id}/forms`)}>
                  Open →
                </button>
                <button className="btn-ghost" onClick={() => remove(ws.id)}>
                  Delete
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Layout>
  )
}