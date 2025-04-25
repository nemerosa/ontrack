import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";
import {SlotBuilds} from "./SlotBuilds";
import {SlotPipelineTable} from "./SlotPipelineTable";

export class SlotPage {
    constructor(page, slot) {
        this.page = page
        this.slot = slot
    }

    async goTo() {
        await this.page.goto(`${this.slot.ontrack.connection.ui}/extension/environments/slot/${this.slot.id}`)
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

    async delete() {
        const button = this.page.getByRole('button', {name: 'Delete slot'})
        await expect(button).toBeVisible()
        await button.click()
        await confirmBox(this.page, "Deleting slot", {okText: "Delete"})
    }

    async getSlotBuilds() {
        const slotBuildsSection = this.page.getByTestId("slotBuilds")
        await expect(slotBuildsSection).toBeVisible()
        return new SlotBuilds(this.page, this.slot)
    }

    async getSlotPipelineTable() {
        const table = new SlotPipelineTable(this.page, this.slot)
        await table.expectToBeVisible()
        return table
    }
}