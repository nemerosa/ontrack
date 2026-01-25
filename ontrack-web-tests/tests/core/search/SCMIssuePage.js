import {expect} from "@playwright/test";

export class SCMIssuePage {

    constructor(page, ontrack, issueKey, project) {
        this.page = page
        this.ontrack = ontrack
        this.issueKey = issueKey
        this.project = project
    }

    async expectOnPage({issueTitle, commitId}) {
        await expect(this.page.getByText(issueTitle, {exact: true})).toBeVisible()
        await expect(this.page.getByRole('link', {name: this.issueKey})).toBeVisible()
        await expect(this.page.getByRole('link', {name: this.project.name, exact: true})).toBeVisible()
        await expect(this.page.getByRole('link', {name: commitId})).toBeVisible()
    }

    async expectBranchInfo({scmBranch, build}) {
        await expect(this.page.getByRole('link', {name: scmBranch, exact: true})).toBeVisible()
        await expect(this.page.getByRole('link', {name: build, exact: true})).toBeVisible()
    }

}