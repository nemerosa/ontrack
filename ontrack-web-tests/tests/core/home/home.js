import {LegacyHomePage} from "../legacy/legacyHome";
import {expect} from "@playwright/test";
import {EnvironmentsPage} from "../../extensions/environments/Environments";

export class HomePage {

    constructor(page) {
        this.page = page
    }

    async checkOnPage() {
        await expect(this.page.getByRole('button', {name: 'New project'})).toBeVisible()
    }

    async legacyHome() {
        await this.page.getByRole('link', {name: 'Legacy home'}).click()
        return new LegacyHomePage(this.page)
    }

    async newProject({name, description, disabled}) {
        await this.page.getByRole('button', {name: 'New project'}).click()
        await expect(this.page.getByPlaceholder('Project name')).toBeVisible()
        await this.page.getByPlaceholder('Project name').fill(name)
        if (description) await this.page.getByPlaceholder('Project description').fill(name)
        if (disabled === true) await this.page.getByLabel('Disabled').click()
        await this.page.getByRole('button', {name: 'OK'}).click()
    }

    async selectEnvironments() {
        await this.page.getByRole('button', {name: 'Environments'}).click()
        return new EnvironmentsPage(this.page)
    }

}