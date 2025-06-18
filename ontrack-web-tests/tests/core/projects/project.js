import {expect} from "@playwright/test";
import {expectOnPage} from "../../support/page-utils";

export class ProjectPage {

    constructor(page, ontrack, project) {
        this.page = page
        this.project = project
        this.ontrack = ontrack
    }

    async expectOnPage() {
        await expectOnPage(this.page, "project")
        await expect(this.page.getByText(this.project.name)).toBeVisible()
    }

    async goTo() {
        await this.page.goto(`${this.ontrack.connection.ui}/project/${this.project.id}`)
        await this.expectOnPage()
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

    async newBranch({name, description, disabled}) {
        await this.page.getByRole('button', {name: 'New branch'}).click()
        await expect(this.page.getByPlaceholder('Branch name')).toBeVisible()
        await this.page.getByPlaceholder('Branch name').fill(name)
        if (description) await this.page.getByPlaceholder('Branch description').fill(name)
        if (disabled === true) await this.page.getByLabel('Disabled').click()
        await this.page.getByRole('button', {name: 'OK'}).click()
    }

    async expectBranchToBePresent(branchName) {
        await expect(this.page.getByRole('link', {name: branchName, exact: true})).toBeVisible()
    }

}