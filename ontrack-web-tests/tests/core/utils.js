export const readClipboard = async (page) => {
    return await page.evaluate(() => {
        return navigator.clipboard.readText()
    })
}
