import { create } from 'zustand'
import { api } from '../api/client'
import type { Field, FieldType, Form } from '../types'

interface FormStore {
  forms: Form[]
  fields: Field[]
  loading: boolean
  error: string | null
  fetchForms: (workspaceId: string) => Promise<void>
  fetchFields: (formId: string) => Promise<void>
  createForm: (workspaceId: string, data: { title: string; description?: string }) => Promise<Form>
  updateForm: (id: string, data: { title: string; description?: string; published: boolean }) => Promise<Form>
  removeForm: (id: string) => Promise<void>
  addField: (formId: string, data: { label: string; type: FieldType; required?: boolean; displayOrder?: number }) => Promise<Field>
  updateField: (id: string, data: { label: string; type: FieldType; required?: boolean; displayOrder?: number }) => Promise<Field>
  removeField: (id: string) => Promise<void>
}

export const useFormStore = create<FormStore>((set) => ({
  forms: [],
  fields: [],
  loading: false,
  error: null,

  fetchForms: async (workspaceId) => {
    set({ loading: true, error: null })
    try {
      const forms = await api.forms.list(workspaceId)
      set({ forms, loading: false })
    } catch (e) {
      set({ error: String(e), loading: false })
    }
  },

  fetchFields: async (formId) => {
    set({ loading: true, error: null })
    try {
      const fields = await api.fields.list(formId)
      set({ fields, loading: false })
    } catch (e) {
      set({ error: String(e), loading: false })
    }
  },

  createForm: async (workspaceId, data) => {
    const form = await api.forms.create(workspaceId, data)
    set((s) => ({ forms: [...s.forms, form] }))
    return form
  },

  updateForm: async (id, data) => {
    const form = await api.forms.update(id, data)
    set((s) => ({ forms: s.forms.map((f) => (f.id === id ? form : f)) }))
    return form
  },

  removeForm: async (id) => {
    await api.forms.delete(id)
    set((s) => ({ forms: s.forms.filter((f) => f.id !== id) }))
  },

  addField: async (formId, data) => {
    const field = await api.fields.create(formId, data)
    set((s) => ({ fields: [...s.fields, field].sort((a, b) => a.displayOrder - b.displayOrder) }))
    return field
  },

  updateField: async (id, data) => {
    const field = await api.fields.update(id, data)
    set((s) => ({
      fields: s.fields.map((f) => (f.id === id ? field : f)).sort((a, b) => a.displayOrder - b.displayOrder),
    }))
    return field
  },

  removeField: async (id) => {
    await api.fields.delete(id)
    set((s) => ({ fields: s.fields.filter((f) => f.id !== id) }))
  },
}))