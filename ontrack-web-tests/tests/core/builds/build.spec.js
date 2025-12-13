const {login} = require("../login");
const {BuildPage} = require("../builds/BuildPage");
const {BranchPage} = require("../branches/branch");
const {test} = require("../../fixtures/connection");

test('build page', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild()
    // Login
    await login(page, ontrack)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()
})

test('build page with links', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild()

    // Link
    const targetProject = await ontrack.createProject()
    const targetBranch = await targetProject.createBranch()
    const target = await targetBranch.createBuild()
    await build.linkTo(target)

    // Login
    await login(page, ontrack)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()
})

test('build page with validations without a type', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp()
    const build = await branch.createBuild()
    await build.validate(validationStamp, {status: "FAILED"})

    // Login
    await login(page, ontrack)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()
})

test('graph of links between builds', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild()

    // Link
    const targetProject = await ontrack.createProject()
    const targetBranch = await targetProject.createBranch()
    const target = await targetBranch.createBuild()
    await build.linkTo(target)

    // Login
    await login(page, ontrack)

    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()

    // Navigating to the links
    const buildLinks = await buildPage.goToLinks()

    // We expect the graph view
    await buildLinks.expectOnGraphView()
    await buildLinks.expectBuildGraphNodeVisible(build)
    await buildLinks.expectBuildGraphNodeVisible(target)

    // Switching to the tree view
    await buildLinks.switchView()
    await buildLinks.expectOnTreeView()
    await buildLinks.expectBuildTreeNodeVisible(build)
    await buildLinks.expectBuildTreeNodeVisible(target)

    // Switching to the graph view again
    await buildLinks.switchView()
    await buildLinks.expectOnGraphView()
    await buildLinks.expectBuildGraphNodeVisible(build)
    await buildLinks.expectBuildGraphNodeVisible(target)
})

test('deleting a build', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild()
    // Login
    await login(page, ontrack)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()

    // Deleting the build
    await buildPage.deleteBuild()

    // Checking we are on the branch page
    const branchPage = new BranchPage(page, branch)
    await branchPage.checkOnPage()
})

test('updating a build', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild('initial-name')

    // Login
    await login(page, ontrack)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()

    // Editing the build
    await buildPage.update({
        name: 'new-name',
        description: 'Some new description',
    })

    // Checking that the page has been updated
    await buildPage.assertName('new-name')
    await buildPage.assertDescription('Some new description')
})
