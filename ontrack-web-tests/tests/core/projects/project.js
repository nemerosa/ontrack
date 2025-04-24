import {expect} from "@playwright/test";

export class ProjectPage {

    constructor(page, ontrack, project) {
        this.page = page
        this.project = project
        this.ontrack = ontrack
    }

    async goTo() {
        await this.page.goto(`${this.ontrack.connection.ui}/project/${this.project.id}`)
        await expect(this.page.getByText(this.project.name)).toBeVisible()
    }

    async checkNoDisabledBanner() {
        await expect(this.getDisabledBanner()).not.toBeVisible()
    }

    async checkDisabledBanner() {
        await expect(this.getDisabledBanner()).toBeVisible()
    }

    async disableProject() {
        await this.page.getByRole('button', {name: "Disable project"}).click()
    }

    async enableProject() {
        await this.page.getByRole('button', {name: "Enable project"}).click()
    }

    getDisabledBanner() {
        return this.page.getByTestId("banner-disabled")
    }

}