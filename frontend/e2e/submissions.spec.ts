import { test, expect } from '@playwright/test'
import { uid, createFormAndOpenSubmissions, createFormWithFileFieldAndOpenSubmissions } from './helpers'

test.describe('Submissions', () => {
  test('shows the submission form with all fields', async ({ page }) => {
    const id = uid()
    await createFormAndOpenSubmissions(page, id)

    await expect(page.getByText('Organisation')).toBeVisible()
    await expect(page.getByText('Country')).toBeVisible()
    await expect(page.getByText('Submit a response')).toBeVisible()
  })

  test('shows validation banner when required field is empty on submit', async ({ page }) => {
    const id = uid()
    await createFormAndOpenSubmissions(page, id)
    await page.getByRole('button', { name: 'Submit response' }).click()

    await expect(page.getByText(/required field/i)).toBeVisible()
  })

  test('submits a response and displays it with human-readable labels', async ({ page }) => {
    const id = uid()
    await createFormAndOpenSubmissions(page, id)
    await page.getByRole('textbox').first().fill('Transparency International')
    await page.getByRole('combobox').selectOption('UK')
    await page.getByRole('button', { name: 'Submit response' }).click()

    const row = page.locator('details.submission-row').first()
    await expect(row).toBeVisible()
    await row.click()

    await expect(page.locator('.submission-data__label').filter({ hasText: 'Organisation' })).toBeVisible()
    await expect(page.locator('.submission-data__value').filter({ hasText: 'Transparency International' })).toBeVisible()
    await expect(page.locator('.submission-data__label').filter({ hasText: 'Country' })).toBeVisible()
    await expect(page.locator('.submission-data__value').filter({ hasText: 'UK' })).toBeVisible()
  })

  test('submission count increments after each submission', async ({ page }) => {
    const id = uid()
    await createFormAndOpenSubmissions(page, id)

    await page.getByRole('textbox').first().fill('First Org')
    await page.getByRole('button', { name: 'Submit response' }).click()
    await expect(page.locator('.section-count')).toHaveText('1')

    await page.getByRole('textbox').first().fill('Second Org')
    await page.getByRole('button', { name: 'Submit response' }).click()
    await expect(page.locator('.section-count')).toHaveText('2')
  })

  test('back to Forms button navigates to the forms page', async ({ page }) => {
    const id = uid()
    await createFormAndOpenSubmissions(page, id)
    await page.getByRole('button', { name: '← back to Forms' }).click()

    await expect(page).toHaveURL(/\/workspaces\//)
    await expect(page.getByRole('heading', { name: 'Forms' })).toBeVisible()
  })

  test('renders a file input for FILE fields', async ({ page }) => {
    const id = uid()
    await createFormWithFileFieldAndOpenSubmissions(page, id)

    await expect(page.getByText('Attachment')).toBeVisible()
    await expect(page.locator('input[type="file"]')).toBeVisible()
  })

  test('uploads a file and shows a Download button in the submission', async ({ page }) => {
    const id = uid()
    await createFormWithFileFieldAndOpenSubmissions(page, id)

    await page.locator('input[type="file"]').setInputFiles({
      name: 'test-upload.txt',
      mimeType: 'text/plain',
      buffer: Buffer.from('hello from playwright'),
    })
    await page.getByRole('button', { name: 'Submit response' }).click()

    const row = page.locator('details.submission-row').first()
    await expect(row).toBeVisible()
    await row.click()

    await expect(page.locator('.submission-data__label').filter({ hasText: 'Attachment' })).toBeVisible()
    const downloadLink = page.locator('a.btn-download-icon[download]')
    await expect(downloadLink).toBeVisible()
    // href should point to the download endpoint with the file key
    const href = await downloadLink.getAttribute('href')
    expect(href).toContain('/api/files/download?key=')
    expect(href).toContain('test-upload.txt')
  })
})