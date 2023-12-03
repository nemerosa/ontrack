// @ts-check
const {test, expect} = require('@playwright/test');
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {BranchPage} = require("../branches/branch");

test('changing status', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp()
    const build = await branch.createBuild()
    const run = await build.validate(validationStamp, {status: "FAILED"})
    // Login
    await login(page)
    // Navigating to the branch
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()
    // Displaying the validation run history dialog
    const runHistoryDialog = await branchPage.validationRunHistory(build, validationStamp)
    // TODO Changing the validation status
    // TODO Checking the validation status has changed
})
