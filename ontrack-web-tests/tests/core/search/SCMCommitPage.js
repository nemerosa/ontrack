import {expect} from "@playwright/test";

export class SCMCommitPage {

    constructor(page, ontrack, commitId, project) {
        this.page = page
        this.ontrack = ontrack
        this.commitId = commitId
        this.project = project
    }

    async expectOnPage(commitMessage) {
        await expect(this.page.getByText(commitMessage, {exact: true})).toBeVisible({timeout: 10_000})
        await expect(this.page.getByRole('link', {name: this.commitId})).toBeVisible()
        await expect(this.page.getByRole('link', {name: this.project.name, exact: true})).toBeVisible()
    }

    async expectBranchInfo({scmBranch, build}) {
        await expect(this.page.getByRole('link', {name: scmBranch, exact: true})).toBeVisible()
        await expect(this.page.getByRole('link', {name: build, exact: true})).toBeVisible()
    }

}