import {expect} from "@playwright/test";

export class SubscriptionsPage {

    constructor(page) {
        this.page = page
    }

    async selectToolsSubscriptions() {
        this.page.getByText("Tools").click()
        this.page.getByText("Subscriptions").click()
        await expect(this.page.getByRole("button", {name: "Create subscription"})).toBeVisible()
    }

}