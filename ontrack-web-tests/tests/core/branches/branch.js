const {ui} = require("@ontrack/connection");
const {expect} = require("@playwright/test");
const {ValidationRunHistoryDialog} = require("../validationRuns/ValidationRunHistoryDialog");

class BranchPage {
    constructor(page, branch) {
        this.page = page;
        this.branch = branch;
    }

    async goTo() {
        await this.page.goto(`${ui()}/branch/${this.branch.id}`)
        await expect(this.page.getByText(this.branch.name)).toBeVisible()
        // Loading finished
        await expect(this.page.getByTestId('loading-builds')).toBeHidden()
    }

    async validationRunHistory(build, validationStamp) {
        const cell = this.page.getByTestId(`${build.id}-${validationStamp.id}`);
        await expect(cell).toBeVisible()
        await cell.click()
        const dialog = new ValidationRunHistoryDialog(this.page, build, validationStamp)
        await dialog.waitFor()
        return dialog
    }
}


module.exports = {BranchPage}
