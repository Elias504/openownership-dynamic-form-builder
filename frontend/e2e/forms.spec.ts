import { test, expect } from '@playwright/test'
import { uid, createWorkspaceAndOpen } from './helpers'

test.describe('Forms', () => {
  test('shows empty state when no forms exist', async ({ page }) => {
    const id = uid()
    await createWorkspaceAndOpen(page, id)
    await expect(page.getByText('No forms in this workspace')).toBeVisible()
  })

  test('creates a form and shows it in the list', async ({ page }) => {
    const id = uid()
    await createWorkspaceAndOpen(page, id)
    await page.getByRole('button', { name: '+ New Form' }).click()
    await page.getByPlaceholder(/Beneficial/i).fill(`Survey ${id}`)
    await page.getByRole('button', { name: 'Create Form' }).click()

    await expect(page.getByText(`Survey ${id}`)).toBeVisible()
  })

  test('navigates to the form builder', async ({ page }) => {
    const id = uid()
    await createWorkspaceAndOpen(page, id)
    await page.getByRole('button', { name: '+ New Form' }).click()
    await page.getByPlaceholder(/Beneficial/i).fill(`Form ${id}`)
    await page.getByRole('button', { name: 'Create Form' }).click()
    await page.locator('.card').filter({ hasText: `Form ${id}` }).getByRole('button', { name: 'Builder' }).click()

    await expect(page).toHaveURL(/\/builder$/)
    await expect(page.getByRole('heading', { name: `Form ${id}` })).toBeVisible()
  })

  test('navigates to the submissions page', async ({ page }) => {
    const id = uid()
    await createWorkspaceAndOpen(page, id)
    await page.getByRole('button', { name: '+ New Form' }).click()
    await page.getByPlaceholder(/Beneficial/i).fill(`Form ${id}`)
    await page.getByRole('button', { name: 'Create Form' }).click()
    await page.locator('.card').filter({ hasText: `Form ${id}` }).getByRole('button', { name: 'Submissions' }).click()

    await expect(page).toHaveURL(/\/submissions$/)
    await expect(page.getByRole('heading', { name: /Form/ })).toBeVisible()
  })

  test('deletes a form', async ({ page }) => {
    const id = uid()
    await createWorkspaceAndOpen(page, id)
    await page.getByRole('button', { name: '+ New Form' }).click()
    await page.getByPlaceholder(/Beneficial/i).fill(`Form ${id}`)
    await page.getByRole('button', { name: 'Create Form' }).click()
    await page.locator('.card').filter({ hasText: `Form ${id}` }).getByRole('button', { name: 'Delete' }).click()

    await expect(page.getByText(`Form ${id}`)).not.toBeVisible()
  })
})