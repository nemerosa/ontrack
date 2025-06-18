import {expect} from "@playwright/test";

export const expectOnPage = async (page, pageId) => {
    await expect(page.locator(`[data-page-id="page-${pageId}"]`)).toBeVisible()
}
