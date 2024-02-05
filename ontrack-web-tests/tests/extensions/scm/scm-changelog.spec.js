// @ts-check
const {test} = require('@playwright/test')
const {login} = require("../../core/login");
const {BranchPage} = require("../../core/branches/branch");
const {provisionChangeLog} = require("./scm");


test("SCM change log", async ({page}) => {
    // Provisioning
    const {from, to} = await provisionChangeLog()

    // Login & going to the branch page
    await login(page)
    const branchPage = new BranchPage(page, from.branch)
    await branchPage.goTo()

    // Making sure the change log button does exist and is disabled
    await branchPage.checkChangeLogButtonPresent({disabled: true})

    // Selects the builds
    await branchPage.selectBuild(from)
    await branchPage.selectBuild(to)

    // Making sure the change log button does exist and is NOT disabled
    await branchPage.checkChangeLogButtonPresent({disabled: false})

    // Going to the change log page
    const changeLogPage = await branchPage.goToChangeLog()

    // Expecting the build sections to be visible
    await changeLogPage.checkBuildFrom(from)
    await changeLogPage.checkBuildTo(to)
})
