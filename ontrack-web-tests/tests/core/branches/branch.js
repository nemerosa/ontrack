const {ui} = require("@ontrack/connection");
const {expect} = require("@playwright/test");
const {ValidationRunHistoryDialog} = require("../validationRuns/ValidationRunHistoryDialog");
const {SCMChangeLogPage} = require("../../extensions/scm/scm");

class BranchPage {
    constructor(page, branch) {
        this.page = page;
        this.branch = branch;
        this.changeLogButton = this.page.getByRole('button', {name: 'Change log', exact: true})
    }

    async goTo() {
        await this.page.goto(`${ui()}/branch/${this.branch.id}`)
        await expect(this.page.getByText(this.branch.name)).toBeVisible()
        // Loading finished
        await expect(this.page.getByTestId('loading-builds')).toBeHidden()
    }

    async checkChangeLogButtonPresent({disabled}) {
        await expect(this.changeLogButton).toBeVisible()
        if (disabled) {
            await expect(this.changeLogButton).toBeDisabled()
        } else {
            await expect(this.changeLogButton).toBeEnabled()
        }
    }

    async selectBuild({id}) {
        await this.page.locator(`#range-${id}`).click()
    }

    async goToChangeLog() {
        await this.changeLogButton.click()
        const changeLogPage = new SCMChangeLogPage(this.page)
        await changeLogPage.checkDisplayed()
        return changeLogPage
    }

    async validationRunHistory(run) {
        const {build, validationStamp} = run
        const cell = this.page.getByTestId(`${build.id}-${validationStamp.id}`);
        await expect(cell).toBeVisible()
        await cell.click()
        const dialog = new ValidationRunHistoryDialog(this.page, run)
        await dialog.waitFor()
        return dialog
    }
}


module.exports = {BranchPage}
