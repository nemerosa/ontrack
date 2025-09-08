const {login} = require("../login");
const {BranchPage} = require("./branch");
const {test} = require("../../fixtures/connection");
const {waitUntilCondition} = require("../../support/timing");
const {generate} = require("@ontrack/utils");
const {ProjectPage} = require("../projects/project");

test('branch creation', async ({page, ontrack}) => {
    const project = await ontrack.createProject()

    await login(page, ontrack)

    const projectPage = new ProjectPage(page, ontrack, project)
    await projectPage.goTo()

    const branchName = generate("b-")
    await projectPage.newBranch({name: branchName})

    await projectPage.expectBranchToBePresent(branchName)
})

test('branch disabling and enabling', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    let branch = await project.createBranch()

    await login(page, ontrack)
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    // Checking that the branch is correctly enabled (using the API)
    await waitUntilCondition({
        page,
        condition: async () => {
            const b = await ontrack.getBranchById(branch.id)
            return !b.disabled
        },
        message: `Branch ${branch.name} is enabled`
    })

    // Checking that there is NO banner showing that the branch is disabled
    await branchPage.checkNoDisabledBanner()

    // Disabling the branch
    await branchPage.disableBranch()

    // Checking that the branch is correctly disabled (using the API)
    await waitUntilCondition({
        page,
        condition: async () => {
            const b = await ontrack.getBranchById(branch.id)
            return b.disabled
        },
        message: `Branch ${branch.name} is disabled`
    })

    // Checking that there IS a banner showing that the branch is disabled
    await branchPage.checkDisabledBanner()

    // Enabling the branch again
    await branchPage.enableBranch()

    // Checking that the branch is correctly enabled (using the API)
    await waitUntilCondition({
        page,
        condition: async () => {
            const b = await ontrack.getBranchById(branch.id)
            return !b.disabled
        },
        message: `Branch ${branch.name} is enabled`
    })

    // Checking that there is NO banner showing that the branch is disabled
    await branchPage.checkNoDisabledBanner()
})

test('deleting a branch', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    // Login
    await login(page, ontrack)
    // Navigating to the branch
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    // Deleting the branch
    await branchPage.deleteBranch()

    // Checking we are on the project page
    const projectPage = new ProjectPage(page, ontrack, project)
    await projectPage.expectOnPage()
})
