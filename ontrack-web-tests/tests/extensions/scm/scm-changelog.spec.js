// @ts-check
const {test} = require('@playwright/test')
const {login} = require("../../core/login");
const {BranchPage} = require("../../core/branches/branch");
const {provisionChangeLog, commits} = require("./scm");


test("SCM change log", async ({page}) => {
    // Provisioning
    const {from, to, mockSCMContext, builds} = await provisionChangeLog()

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

    // Expecting some commits to show
    await changeLogPage.checkCommitMessage(commits[4], {present: true})
    await changeLogPage.checkCommitMessage(commits[3], {present: true})
    await changeLogPage.checkCommitMessage(commits[2], {present: true})
    await changeLogPage.checkCommitMessage(commits[1], {present: true})

    // ... some not
    await changeLogPage.checkCommitMessage(commits[0], {present: false})

    // Checks build links
    await changeLogPage.checkCommitBuild(commits[4], mockSCMContext, to, {expected: true})
    await changeLogPage.checkCommitBuild(commits[3], mockSCMContext, builds[2], {expected: true})
    await changeLogPage.checkCommitBuild(commits[2], mockSCMContext, builds[1], {expected: true})
    await changeLogPage.checkCommitBuild(commits[1], mockSCMContext, builds[1], {expected: false})
})
