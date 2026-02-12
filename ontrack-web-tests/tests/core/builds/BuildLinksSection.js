import {expect} from "@playwright/test";

export class BuildLinksSection {

    constructor(page, build, section) {
        this.page = page
        this.build = build
        this.section = section
    }

    async isProjectLinkVisible({project, visible}) {
        await expect(this.section.getByRole('link', {name: project, exact: true})).toBeVisible({visible})
    }

    async filterByProjectName({project}) {
        const projectFilter = this.section.getByPlaceholder("Project filter", {exact: true})
        await projectFilter.fill(project)
        await projectFilter.press('Enter')
    }

    async clearFilterByProjectName() {
        await this.section.getByRole('button', { name: 'close-circle' }).click()
    }

}