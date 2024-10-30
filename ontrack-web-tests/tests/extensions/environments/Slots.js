import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";

export const createSlot = async (ontrack) => {
    const environment = await ontrack.environments.createEnvironment({})
    const project = await ontrack.createProject()
    const slot = await environment.createSlot({project})
    return {
        environment,
        project,
        slot,
    }
}

export class SlotPage {
    constructor(page, slot) {
        this.page = page
        this.slot = slot
    }

    async goTo() {
        await this.page.goto(`${ui()}/extension/environments/slot/${this.slot.id}`)
        await expect(this.page.getByText('Slot details', {exact: true})).toBeVisible()
        await expect(this.page.getByText(`Slot ${this.slot.environment.name} - ${this.slot.project.name}`, {exact: true})).toBeVisible()
    }

    async addAdmissionRule({rule, providedName, name, description, config}) {
        await this.page.getByRole('button', {name: "Add admission rule"}).click()
        await expect(this.page.getByText('Rule configuration', {exact: true})).toBeVisible()

        if (providedName) {
            await this.page.getByLabel('Name').fill(providedName)
        }

        await this.page.getByLabel('Description').fill(description)
        await this.page.getByLabel('Rule configuration').click()
        await this.page.getByText(rule, {exact: true}).click()

        if (config) {
            config()
        }

        await this.page.getByRole('button', {name: "OK"}).click()

        await expect(this.page.getByRole('button', {name: "OK"})).not.toBeVisible()
        await expect(this.page.getByText(name, {exact: true})).toBeVisible()
        await expect(this.page.getByText(description, {exact: true})).toBeVisible()
    }
}