import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";
import {confirmBox} from "../../support/confirm";

export class ProjectPage {

    constructor(page, project) {
        this.page = page
        this.project = project
    }

    async checkOnPage() {
        await expect(this.page.getByText(this.project.name)).toBeVisible()
    }

    async goTo() {
        await this.page.goto(`${ui()}/project/${this.project.id}`)
        await this.checkOnPage()
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

    async deleteProject() {
        const button = this.page.getByRole('button', {name: 'Delete project'})
        await expect(button).toBeVisible()
        await button.click()
        await confirmBox(this.page, "Delete project", {okText: "Delete"})
    }
}