import {expect} from "@playwright/test";

export const confirmBox = async (page, title) => {
    await expect(page.getByText(title)).toBeVisible()
    await page.getByRole("button", { name: "OK" }).click()
}