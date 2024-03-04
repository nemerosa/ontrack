export const selectUserMenu = async (page, text) => {
    // Opens the menu
    await page.locator('#user-menu').click()
    // Waits for the next and click
    await page.getByText(text, {exact: true}).click()
}