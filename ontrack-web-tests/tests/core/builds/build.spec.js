const {test} = require("@playwright/test");
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {BuildPage} = require("../builds/build");

test('build page', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild()
    // Login
    await login(page)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()
})

test('build page with links', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const build = await branch.createBuild()

    // Link
    const targetProject = await ontrack().createProject()
    const targetBranch = await targetProject.createBranch()
    const target = await targetBranch.createBuild()
    await build.linkTo(target)

    // Login
    await login(page)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()
})

test('build page with validations without a type', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp()
    const build = await branch.createBuild()
    await build.validate(validationStamp, {status: "FAILED"})

    // Login
    await login(page)
    // Navigating to the build
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()
})