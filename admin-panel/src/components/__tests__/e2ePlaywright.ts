import { test, expect } from '@playwright/test';


test("Download a plugin from library and check if it's code matches in my plugins.", async ({ page }) => {
  await page.goto('http://localhost:5173/');
  await page.locator( '#alert_email' ).fill('admin@pjatk.pl')
  await page.locator( '#alert_password' ).fill('admin123')
  await page.locator( '#alert_sign_in' ).click()
  await page.waitForSelector('#alerts_dashboard')
  await page.goto('http://localhost:5173/plugins_library')
  await page.waitForSelector('#plugins_library_table')

  const libraryPlugin = page
    .locator('#plugins_library_table tbody tr[id]')
    .first()


  const libraryPluginId = await libraryPlugin
    .getAttribute('id')
    .then(libraryPluginId => libraryPluginId?.replace('library_plugin_',''))

  const libraryPluginName = await libraryPlugin
    .locator(`#library_plugin_name_${libraryPluginId}`)
    .textContent() || ''

  await libraryPlugin.locator(`#details_library_plugin_${libraryPluginId}`).click()

  await page
    .waitForFunction(() => {
      const el = document.querySelector('#show_plugin_code')
      return el && el.textContent.trim() !== ''
    })
  const libraryCode = await page.locator('#show_plugin_code').textContent() || ''

  await page.locator("#close_plugin_details_dialog").click()

  await libraryPlugin
    .locator(`#download_plugin_${libraryPluginId}`)
    .click()
  await page.goto('http://localhost:5173/my_plugins')
  await page.waitForSelector('#my_plugins_table')

  const myPluginsTable = page
    .locator('#my_plugins_table tbody tr[id]')
    .getByText(libraryPluginName)

  await myPluginsTable.click()

  await expect(page.locator(`#my_plugin_details_${libraryPluginName}`))
    .toBeEnabled({ timeout: 5000})
    .then(()=> page.locator(`#my_plugin_details_${libraryPluginName}`).click())

  const myCode = await page.locator('#my_plugin_code').inputValue()
  expect(myCode).toContain(libraryCode)

})

test('Check if new user can see alerts with rules after adding to a group.', async  ({ page}) => {
  await page.goto('http://localhost:5173/');
  await page.goto('http://localhost:5173/alerts_history');
  await page.goto('http://localhost:5173/my_plugins');
  await page.goto('http://localhost:5173/charts');
  await page.goto('http://localhost:5173/')
  await page.goto('http://localhost:5173/alerts_history');
  await page.goto('http://localhost:5173/plugins_library');
  await page.goto('http://localhost:5173/my_plugins');
  await page.goto('http://localhost:5173/charts');
  await page.goto('http://localhost:5173/');
  await page.goto('http://localhost:5173/alerts_history');
  await page.goto('http://localhost:5173/plugins_library');
  await page.goto('http://localhost:5173/my_plugins');
  await page.goto('http://localhost:5173/charts');
})
