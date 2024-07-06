import {ui} from "@ontrack/connection";
import {expect} from "@playwright/test";

export class ProjectPage {

    constructor(page, project) {
        this.page = page
        this.project = project
    }

    async goTo() {
        await this.page.goto(`${ui()}/project/${this.project.id}`)
        await expect(this.page.getByText(this.project.name)).toBeVisible()
    }

}