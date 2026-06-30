import { create } from 'zustand'
import { api } from '../api/client'
import type { Workspace } from '../types'

interface WorkspaceStore {
  workspaces: Workspace[]
  loading: boolean
  error: string | null
  fetchAll: () => Promise<void>
  create: (data: { name: string; slug: string }) => Promise<Workspace>
  remove: (id: string) => Promise<void>
}

export const useWorkspaceStore = create<WorkspaceStore>((set) => ({
  workspaces: [],
  loading: false,
  error: null,

  fetchAll: async () => {
    set({ loading: true, error: null })
    try {
      const workspaces = await api.workspaces.list()
      set({ workspaces, loading: false })
    } catch (e) {
      set({ error: String(e), loading: false })
    }
  },

  create: async (data) => {
    const workspace = await api.workspaces.create(data)
    set((s) => ({ workspaces: [...s.workspaces, workspace] }))
    return workspace
  },

  remove: async (id) => {
    await api.workspaces.delete(id)
    set((s) => ({ workspaces: s.workspaces.filter((w) => w.id !== id) }))
  },
}))