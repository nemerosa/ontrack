import {expect} from "@playwright/test";

export const confirmBox = async (page, title, options = {okText: "OK"}) => {
    await expect(page.getByText(title)).toBeVisible()
    const okButton = page.getByRole("button", {name: options.okText, exact: true})
    await okButton.click()
    await expect(okButton).not.toBeVisible()
}