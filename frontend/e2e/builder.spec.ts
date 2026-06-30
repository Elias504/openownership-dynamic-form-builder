import { test, expect } from '@playwright/test'
import { uid, createFormAndOpenBuilder } from './helpers'

test.describe('Form Builder', () => {
  test('shows empty state with no fields', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await expect(page.getByText('No fields yet')).toBeVisible()
  })

  test('adds a text field', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '+ Add Field' }).click()
    await page.getByPlaceholder('e.g. Full name').fill('Full Name')
    await page.getByRole('button', { name: 'Add Field' }).click()

    await expect(page.getByText('Full Name')).toBeVisible()
    await expect(page.locator('.badge').filter({ hasText: 'Text' })).toBeVisible()
  })

  test('adds a required field and shows the required badge', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '+ Add Field' }).click()
    await page.getByPlaceholder('e.g. Full name').fill('Email Address')
    await page.getByLabel('Required').check()
    await page.getByRole('button', { name: 'Add Field' }).click()

    await expect(page.getByText('Email Address')).toBeVisible()
    await expect(page.locator('.badge').filter({ hasText: 'Required' })).toBeVisible()
  })

  test('adds a dropdown field with options', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '+ Add Field' }).click()
    await page.getByPlaceholder('e.g. Full name').fill('Country')
    await page.locator('select[name="type"]').selectOption('SELECT')
    await page.getByPlaceholder(/Option A/i).fill('UK\nUS\nFrance')
    await page.getByRole('button', { name: 'Add Field' }).click()

    await expect(page.getByText('Country')).toBeVisible()
    await expect(page.locator('.badge').filter({ hasText: 'Dropdown' })).toBeVisible()
    await expect(page.getByText('(3 options)')).toBeVisible()
  })

  test('adds a file field', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '+ Add Field' }).click()
    await page.getByPlaceholder('e.g. Full name').fill('Attachment')
    await page.locator('select[name="type"]').selectOption('FILE')
    await page.getByRole('button', { name: 'Add Field' }).click()

    await expect(page.getByText('Attachment')).toBeVisible()
    await expect(page.locator('.badge').filter({ hasText: 'File' })).toBeVisible()
  })

  test('removes a field', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '+ Add Field' }).click()
    await page.getByPlaceholder('e.g. Full name').fill('Temporary Field')
    await page.getByRole('button', { name: 'Add Field' }).click()
    await expect(page.getByText('Temporary Field')).toBeVisible()

    await page.getByRole('button', { name: 'Remove' }).click()
    await expect(page.getByText('Temporary Field')).not.toBeVisible()
    await expect(page.getByText('No fields yet')).toBeVisible()
  })

  test('cancel hides the add field form', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '+ Add Field' }).click()
    await expect(page.getByPlaceholder('e.g. Full name')).toBeVisible()
    await page.getByRole('button', { name: 'Cancel' }).click()
    await expect(page.getByPlaceholder('e.g. Full name')).not.toBeVisible()
  })

  test('back to Forms button navigates to forms page', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: '← back to Forms' }).click()

    await expect(page).toHaveURL(/\/workspaces\//)
    await expect(page.getByRole('heading', { name: 'Forms' })).toBeVisible()
  })

  test('to Submissions button navigates to submissions page', async ({ page }) => {
    const id = uid()
    await createFormAndOpenBuilder(page, id)
    await page.getByRole('button', { name: 'to Submissions →' }).click()

    await expect(page).toHaveURL(/\/submissions$/)
  })
})
