import { create } from 'zustand'
import { api } from '../api/client'
import type { Submission } from '../types'

interface SubmissionStore {
  submissions: Submission[]
  loading: boolean
  error: string | null
  fetchByForm: (formId: string) => Promise<void>
  submit: (formId: string, data: Record<string, unknown>) => Promise<Submission>
}

export const useSubmissionStore = create<SubmissionStore>((set) => ({
  submissions: [],
  loading: false,
  error: null,

  fetchByForm: async (formId) => {
    set({ loading: true, error: null })
    try {
      const submissions = await api.submissions.list(formId)
      set({ submissions, loading: false })
    } catch (e) {
      set({ error: String(e), loading: false })
    }
  },

  submit: async (formId, data) => {
    const submission = await api.submissions.create(formId, data)
    set((s) => ({ submissions: [submission, ...s.submissions] }))
    return submission
  },
}))