import {expect} from "@playwright/test";

export class SearchPage {

    constructor(page, ontrack) {
        this.page = page
        this.ontrack = ontrack
    }

    async expectOnPage() {
    }

    async expectProjectResultPresent(name) {
        const link = this.page.getByRole('link', {name, exact: true})
        await expect(link).toBeVisible({timeout: 20_000}) // Waiting a bit longer, in case ES is not ready yet
    }

    async expectScmCommitResultPresent({commitId}) {
        const link = this.page.getByRole('link', {name: commitId, exact: true})
        await expect(link).toBeVisible({timeout: 20_000}) // Waiting a bit longer, in case ES is not ready yet
    }

    async expectScmIssueResultPresent({issueKey}) {
        const link = this.page.getByRole('link', {name: issueKey, exact: true})
        await expect(link).toBeVisible({timeout: 20_000}) // Waiting a bit longer, in case ES is not ready yet
    }

    async clickProjectResult(name) {
        const link = this.page.getByRole('link', {name, exact: true})
        await link.click()
    }

    async clickScmCommitResult({commitId}) {
        const link = this.page.getByRole('link', {name: commitId, exact: true})
        await link.click()
    }

    async clickScmIssueResult({issueKey}) {
        const link = this.page.getByRole('link', {name: issueKey, exact: true})
        await link.click()
    }
}