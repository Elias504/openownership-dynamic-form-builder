import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { useFormStore } from '../store/formStore'
import Layout from '../components/Layout'

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
    <Layout
      title="Forms"
      breadcrumbs={[{ label: 'Workspaces', to: '/' }]}
      action={
        <button
          className={showForm ? 'btn btn-cancel' : 'btn'}
          onClick={() => { setShowForm((v) => !v); reset() }}
        >
          {showForm ? 'Cancel' : '+ New Form'}
        </button>
      }
    >
      {error && <div className="error-banner">{error}</div>}

      {showForm && (
        <div className="form-panel">
          <p className="form-panel__title">New Form</p>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="form-panel__grid form-panel__grid-2">
              <div className="form-group">
                <label className="form-label">
                  Title <span className="form-required">*</span>
                </label>
                <input
                  className="form-input"
                  placeholder="e.g. Beneficial Ownership Declaration"
                  {...register('title', { required: true })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <input
                  className="form-input"
                  placeholder="Optional description"
                  {...register('description')}
                />
              </div>
            </div>
            <div className="form-panel__actions">
              <button type="submit" className="btn">Create Form</button>
            </div>
          </form>
        </div>
      )}

      {loading && <p className="loading-text">Loading forms…</p>}

      {!loading && forms.length === 0 && !showForm && (
        <div className="empty-state">
          <p className="empty-state__title">No forms in this workspace</p>
          <p className="empty-state__text">Create a form to start collecting data.</p>
        </div>
      )}

      <div className="card-grid">
        {forms.map((form) => (
          <div key={form.id} className="card">
            <div className="flex items-start justify-between gap-4">
              <div className="flex-1">
                <div className="flex items-center gap-3 flex-wrap">
                  <p className="card-title">{form.title}</p>
                  {form.published && (
                    <span className="badge badge-green">Published</span>
                  )}
                </div>
                {form.description && (
                  <p className="text-muted text-sm mt-2">{form.description}</p>
                )}
              </div>
              <div className="flex gap-2 items-center" style={{ flexShrink: 0 }}>
                <button className="btn btn-sm" onClick={() => navigate(`/forms/${form.id}/builder`)}>
                  Builder
                </button>
                <button className="btn btn-sm btn-outline" onClick={() => navigate(`/forms/${form.id}/submissions`)}>
                  Submissions
                </button>
                <button className="btn-ghost" onClick={() => removeForm(form.id)}>
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