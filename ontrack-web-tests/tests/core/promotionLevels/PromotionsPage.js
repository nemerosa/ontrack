import {expect} from "@playwright/test";

export class PromotionsPage {

    constructor(page, branch) {
        this.page = page
        this.branch = branch
    }

    async checkOnPage() {
        await expect(this.page.getByText("Promotion levels", {exact: true})).toBeVisible()
        await expect(this.page.getByText(this.branch.name, {exact: true})).toBeVisible()
    }

    async createPromotionLevel({name}) {
        const createPromotionLevel = this.page.getByRole('button', {name: "Create promotion level"})
        await expect(createPromotionLevel).toBeVisible()
        await createPromotionLevel.click()

        const nameField = this.page.getByLabel("Name")
        const descriptionField = this.page.getByLabel("Description")
        const okButton = this.page.getByRole('button', {name: "OK"})

        await expect(nameField).toBeVisible()
        await expect(descriptionField).toBeVisible()

        await nameField.fill(name)
        await okButton.click()
    }

    async checkPromotionLevel({name}) {
        await expect(this.page.getByRole('link', {name: name, exact: true})).toBeVisible()
    }

}
