import { test, expect } from '@playwright/test'
import { uid, createWorkspaceAndOpen } from './helpers'

test.describe('Workspaces', () => {
  test('creates a workspace and shows it in the list', async ({ page }) => {
    const id = uid()
    await page.goto('/')
    await page.getByRole('button', { name: '+ New Workspace' }).click()
    await page.getByPlaceholder('e.g. Anti-Corruption Unit', { exact: true }).fill(`WS ${id}`)
    await page.getByPlaceholder('e.g. anti-corruption', { exact: true }).fill(id)
    await page.getByRole('button', { name: 'Create Workspace' }).click()

    await expect(page.getByText(`WS ${id}`)).toBeVisible()
    await expect(page.locator('.badge').filter({ hasText: id })).toBeVisible()
  })

  test('navigates into a workspace', async ({ page }) => {
    const id = uid()
    await createWorkspaceAndOpen(page, id)

    await expect(page).toHaveURL(/\/workspaces\//)
    await expect(page.getByRole('heading', { name: 'Forms' })).toBeVisible()
  })

  test('deletes a workspace', async ({ page }) => {
    const id = uid()
    await page.goto('/')
    await page.getByRole('button', { name: '+ New Workspace' }).click()
    await page.getByPlaceholder('e.g. Anti-Corruption Unit', { exact: true }).fill(`WS ${id}`)
    await page.getByPlaceholder('e.g. anti-corruption', { exact: true }).fill(id)
    await page.getByRole('button', { name: 'Create Workspace' }).click()

    await page.locator('.card').filter({ hasText: `WS ${id}` }).getByRole('button', { name: 'Delete' }).click()
    await expect(page.getByText(`WS ${id}`)).not.toBeVisible()
  })
})