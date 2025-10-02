const {test, expect} = require("@playwright/test");
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {BranchPage} = require("./branch");
const {BuildPage} = require("../builds/build");
const {ProjectPage} = require("../projects/project");

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

test('deleting a branch', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    // Login
    await login(page)
    // Navigating to the branch
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    // Deleting the branch
    await branchPage.deleteBranch()

    // Checking we are on the project page
    const projectPage = new ProjectPage(page, project)
    await projectPage.checkOnPage()
})

test('many validations for a branch', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild("1.0.0")
    const numberVs = 30
    for (let i = 1; i <= numberVs; i++) {
        const vs = await branch.createValidationStamp(`VS${i}`)
        await build.validate(vs, {})
    }
    // Login
    await login(page)
    // Navigating to the branch
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()
    // Waiting for the VS1 to be visible
    await expect(page.getByText("1.0.0")).toBeVisible()
})
