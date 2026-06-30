export interface BaseDto {
  id: string
  createdAt: string
  createdBy: string
  updatedAt: string
  updatedBy: string
  deletedAt: string | null
  deletedBy: string | null
}

export interface Workspace extends BaseDto {
  name: string
  slug: string
}

export interface Form extends BaseDto {
  workspaceId: string
  title: string
  description?: string
  published: boolean
}

export type FieldType = 'TEXT' | 'TEXTAREA' | 'NUMBER' | 'DATE' | 'SELECT' | 'CHECKBOX' | 'RADIO' | 'FILE'

export interface Field extends BaseDto {
  formId: string
  label: string
  type: FieldType
  required: boolean
  displayOrder: number
  config: Record<string, unknown>
}

export interface Submission extends BaseDto {
  formId: string
  data: Record<string, unknown>
}
