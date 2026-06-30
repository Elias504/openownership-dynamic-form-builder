export interface Workspace {
  id: string
  name: string
  slug: string
  createdAt: string
}

export interface Form {
  id: string
  workspaceId: string
  title: string
  description?: string
  published: boolean
  createdAt: string
}

export type FieldType = 'TEXT' | 'TEXTAREA' | 'NUMBER' | 'DATE' | 'SELECT' | 'CHECKBOX' | 'RADIO' | 'FILE'

export interface Field {
  id: string
  formId: string
  label: string
  type: FieldType
  required: boolean
  displayOrder: number
  config: Record<string, unknown>
}

export interface Submission {
  id: string
  formId: string
  data: Record<string, unknown>
  submittedAt: string
}