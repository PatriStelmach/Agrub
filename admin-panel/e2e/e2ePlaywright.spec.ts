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
  await page.goto('my_plugins')

  const myPluginRow = page
    .locator('#my_plugins_table tbody tr[id]')
    .getByText(libraryPluginName)

  await myPluginRow.click()

  await expect(page.locator(`#my_plugin_details_${libraryPluginName}`))
    .toBeEnabled({ timeout: 5000})
    .then(()=> page.locator(`#my_plugin_details_${libraryPluginName}`).click())

  const myCode = await page.locator('#my_plugin_code').inputValue()
  expect(myCode).toContain(libraryCode)

})

test('Check if new user can see alerts with rules after adding to a group.', async  ({ page}) => {
  await page.goto('http://localhost:5173/');
  await page.locator( '#alert_email' ).fill('admin@pjatk.pl')
  await page.locator( '#alert_password' ).fill('admin123')
  await page.locator( '#alert_sign_in' ).click()
  await page.waitForSelector('#alerts_dashboard')

  await page.goto('http://localhost:5173/team_members')
  //await page.waitForSelector('#add_user_button')
  await page.locator('#add_user_button').click()

  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
  let firstname = ""
  let surname = ""

  for (let i = 0; i < 5; i++) {
    firstname += chars.charAt(Math.floor(Math.random() * chars.length))
    surname += chars.charAt(Math.floor(Math.random() * chars.length))
  }

  const email = (firstname.slice(2)+surname.slice(2)).toLowerCase()

  await page.locator("#firstname").fill(firstname)
  await page.locator("#surname").fill(surname)
  await page.locator("#email").fill(`${email}@pjatk.pl`)
  await page.locator("#password").fill('password123!')
  await page.locator("#confirmPassword").fill('password123!')
  await page.locator("#radio-technician").click()

  await page.locator("#open_tag_list_button").click()
  await page.locator("#tag_TECH").click()
  await page.locator("#submit_user_form").click()

  await page.goto("/groups")
  await page.locator("#group_TECH_card").click()

  await page.locator("#add_new_rule_button").click()
  await page.locator("#originPattern").fill("Local Script")
  await page.locator("#submit_rule_form_button").click()

  await page.goto("/my_plugins")

  const testPluginName = "second checker"

  const myPluginRow = page
    .locator('#my_plugins_table tbody tr[id]')
    .getByText(testPluginName)
  await myPluginRow.click()

  await page.locator(`my_plugin_cron_input_${testPluginName}`).fill("* * * * * *")
  await page.locator("radio-plugin-on").click()
  await page.locator("radio-plugin-off").click()
})
