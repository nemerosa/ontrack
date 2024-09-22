const {test, expect} = require("@playwright/test");
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {BranchPage} = require("./branch");

test('branch disabling and enabling', async ({page}) => {
    const project = await ontrack().createProject()
    let branch = await project.createBranch()

    await login(page)
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    // Checking that the branch is correctly enabled (using the API)
    branch = await ontrack().getBranchById(branch.id)
    expect(branch.disabled).toBeFalsy()

    // Checking that there is NO banner showing that the branch is disabled
    await branchPage.checkNoDisabledBanner()

    // Disabling the branch
    await branchPage.disableBranch()

    // Checking that the branch is correctly disabled (using the API)
    branch = await ontrack().getBranchById(branch.id)
    expect(branch.disabled).toBeTruthy()

    // Checking that there IS a banner showing that the branch is disabled
    await branchPage.checkDisabledBanner()

    // Enabling the branch again
    await branchPage.enableBranch()

    // Checking that the branch is correctly enabled (using the API)
    branch = await ontrack().getBranchById(branch.id)
    expect(branch.disabled).toBeFalsy()

    // Checking that there is NO banner showing that the branch is disabled
    await branchPage.checkNoDisabledBanner()
})
