// @ts-check
const {test, expect} = require('@playwright/test');
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {BranchPage} = require("../branches/branch");
const {generate} = require("@ontrack/utils");

test('changing status', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp()
    const build = await branch.createBuild()
    let run = await build.validate(validationStamp, {status: "FAILED"})
    // Login
    await login(page)
    // Navigating to the branch
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()
    // Displaying the validation run history dialog
    const runHistoryDialog = await branchPage.validationRunHistory(run)
    // Changing the validation status
    const id = generate('id_')
    const message = `Currently investigation the issue ${id}`;
    await runHistoryDialog.selectStatus('Investigating')
    await runHistoryDialog.setDescription(message)
    await runHistoryDialog.addStatus()
    // Checking the validation status has changed in the UI
    await runHistoryDialog.checkStatus('Investigating', message)
    // Checking through the API
    run = await ontrack().getValidationRunById(run.id)
    expect(run.lastStatus.statusID.id).toBe('INVESTIGATING')
})
