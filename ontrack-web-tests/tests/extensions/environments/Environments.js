import {expect} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";

export class EnvironmentsPage {

    constructor(page) {
        this.page = page
    }

    async createEnvironment({name, description, order, tags}) {
        await this.page.getByRole('button', {name: "New environment"}).click()
        const dialog = new EnvironmentDialog(this.page)
        await dialog.set({name, description, order, tags})
        await dialog.ok()
    }

    async checkEnvironmentIsVisible(name) {
        // Getting the ID of the environment
        let environmentId = null
        await expect.poll(async () => {
            console.log(`Getting env with name ${name}`)
            const environment = await ontrack().environments.findEnvironmentByName(name)
            environmentId = environment?.id
            return environmentId
        }).toBeDefined()
        if (!environmentId) throw new Error(`Environment with name ${name} not found`)
        // Card
        const card = this.page.getByTestId(`environment-${environmentId}`)
        // Looking for the name
        await expect(card.getByText(name, {exact: true})).toBeVisible()
    }

}

export class EnvironmentDialog {
    constructor(page) {
        this.page = page
    }

    async set({name, description, order, tags}) {
        await expect(this.page.getByLabel('Name')).toBeVisible()
        await this.page.getByLabel('Name').fill(name)
        await this.page.getByLabel('Description').fill(description)
        await this.page.getByLabel('Order').fill(order.toString())
        const tagsField = this.page.getByLabel('Tags')
        for (const tag of tags) {
            await tagsField.click()
            await tagsField.type(tag)
        }
    }

    async ok() {
        await this.page.getByRole('button', {name: 'OK'}).click()
        // Waiting for the dialog to be gone
        await expect(this.page.getByRole('button', {name: 'OK'})).toHaveCount(0)
    }
}