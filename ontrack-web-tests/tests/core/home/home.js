import {expect} from "@playwright/test";
import {EnvironmentsPage} from "../../extensions/environments/Environments";

export class HomePage {

    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
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
        return new EnvironmentsPage(this.page, this.ontrack)
    }

}