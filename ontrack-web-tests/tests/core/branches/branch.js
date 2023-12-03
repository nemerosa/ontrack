const {ui} = require("@ontrack/connection");
const {expect} = require("@playwright/test");

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
}


module.exports = {BranchPage}
