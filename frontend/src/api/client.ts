import type { Field, FieldType, Form, Submission, Workspace } from '../types'

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const res = await fetch(path, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...options?.headers },
  })
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`)
  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}

export const api = {
  workspaces: {
    list: () => request<Workspace[]>('/api/workspaces'),
    get: (id: string) => request<Workspace>(`/api/workspaces/${id}`),
    create: (data: { name: string; slug: string }) =>
      request<Workspace>('/api/workspaces', { method: 'POST', body: JSON.stringify(data) }),
    update: (id: string, data: { name: string; slug: string }) =>
      request<Workspace>(`/api/workspaces/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: string) => request<void>(`/api/workspaces/${id}`, { method: 'DELETE' }),
  },

  forms: {
    list: (workspaceId: string) => request<Form[]>(`/api/workspaces/${workspaceId}/forms`),
    get: (id: string) => request<Form>(`/api/forms/${id}`),
    create: (workspaceId: string, data: { title: string; description?: string }) =>
      request<Form>(`/api/workspaces/${workspaceId}/forms`, { method: 'POST', body: JSON.stringify(data) }),
    update: (id: string, data: { title: string; description?: string; published: boolean }) =>
      request<Form>(`/api/forms/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: string) => request<void>(`/api/forms/${id}`, { method: 'DELETE' }),
  },

  fields: {
    list: (formId: string) => request<Field[]>(`/api/forms/${formId}/fields`),
    create: (formId: string, data: { label: string; type: FieldType; required?: boolean; displayOrder?: number; config?: Record<string, unknown> }) =>
      request<Field>(`/api/forms/${formId}/fields`, { method: 'POST', body: JSON.stringify(data) }),
    update: (id: string, data: { label: string; type: FieldType; required?: boolean; displayOrder?: number; config?: Record<string, unknown> }) =>
      request<Field>(`/api/fields/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id: string) => request<void>(`/api/fields/${id}`, { method: 'DELETE' }),
  },

  submissions: {
    list: (formId: string) => request<Submission[]>(`/api/forms/${formId}/submissions`),
    get: (id: string) => request<Submission>(`/api/submissions/${id}`),
    create: (formId: string, data: Record<string, unknown>) =>
      request<Submission>(`/api/forms/${formId}/submissions`, { method: 'POST', body: JSON.stringify({ data }) }),
  },
}