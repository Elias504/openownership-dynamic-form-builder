import type { Page } from '@playwright/test'

export function uid(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
}

export async function createWorkspaceAndOpen(page: Page, id: string): Promise<void> {
  await page.goto('/')
  await page.getByRole('button', { name: '+ New Workspace' }).click()
  await page.getByPlaceholder('e.g. Anti-Corruption Unit', { exact: true }).fill(`WS ${id}`)
  await page.getByPlaceholder('e.g. anti-corruption', { exact: true }).fill(id)
  await page.getByRole('button', { name: 'Create Workspace' }).click()
  await page.locator('.card').filter({ hasText: `WS ${id}` }).getByRole('button', { name: 'Open →' }).click()
}

export async function createFormAndOpenBuilder(page: Page, id: string): Promise<void> {
  await createWorkspaceAndOpen(page, id)
  await page.getByRole('button', { name: '+ New Form' }).click()
  await page.getByPlaceholder(/Beneficial/i).fill(`Form ${id}`)
  await page.getByRole('button', { name: 'Create Form' }).click()
  await page.locator('.card').filter({ hasText: `Form ${id}` }).getByRole('button', { name: 'Builder' }).click()
}

export async function createFormAndOpenSubmissions(page: Page, id: string): Promise<void> {
  await createFormAndOpenBuilder(page, id)

  await page.getByRole('button', { name: '+ Add Field' }).click()
  await page.getByPlaceholder('e.g. Full name').fill('Organisation')
  await page.getByLabel('Required').check()
  await page.getByRole('button', { name: 'Add Field' }).click()

  await page.getByRole('button', { name: '+ Add Field' }).click()
  await page.getByPlaceholder('e.g. Full name').fill('Country')
  await page.locator('select[name="type"]').selectOption('SELECT')
  await page.getByPlaceholder(/Option A/i).fill('UK\nUS\nFrance')
  await page.getByRole('button', { name: 'Add Field' }).click()

  // Go back to the forms page, then open submissions from there
  // (so that navigate(-1) on the submissions page correctly returns to forms)
  await page.getByRole('button', { name: '← back to Forms' }).click()
  await page.locator('.card').filter({ hasText: `Form ${id}` }).getByRole('button', { name: 'Submissions' }).click()
}

export async function createFormWithFileFieldAndOpenSubmissions(page: Page, id: string): Promise<void> {
  await createFormAndOpenBuilder(page, id)

  await page.getByRole('button', { name: '+ Add Field' }).click()
  await page.getByPlaceholder('e.g. Full name').fill('Attachment')
  await page.locator('select[name="type"]').selectOption('FILE')
  await page.getByRole('button', { name: 'Add Field' }).click()

  await page.getByRole('button', { name: '← back to Forms' }).click()
  await page.locator('.card').filter({ hasText: `Form ${id}` }).getByRole('button', { name: 'Submissions' }).click()
}