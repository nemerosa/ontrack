import {expect} from "@playwright/test";

export const confirmBox = async (page, title) => {
    await expect(page.getByText(title)).toBeVisible()
    const okButton = page.getByRole("button", {name: "OK"});
    await okButton.click()
    await expect(okButton).not.toBeVisible()
}