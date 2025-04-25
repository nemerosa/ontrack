// @ts-check
const {login} = require("../../core/login");
const {BranchPage} = require("../../core/branches/branch");
const {provisionChangeLog, commits, issues} = require("./scm");
const {generate, trimIndent} = require("@ontrack/utils");
const {ontrack} = require("@ontrack/ontrack");
const {test} = require("../../fixtures/connection");


const doTestSCMChangeLog = async (
    page,
    ontrack,
    issueServiceId = undefined,
    issueServiceIdentifier = undefined
) => {
    // Provisioning
    const {from, to, mockSCMContext, builds} = await provisionChangeLog(
        ontrack,
        issueServiceId,
        issueServiceIdentifier,
    )

    // Login & going to the branch page
    await login(page, ontrack)
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

    /**
     * Commits
     */

    // Expecting the build diff to be there
    await changeLogPage.checkCommitDiffLink()

    // Expecting some commits to show
    await changeLogPage.checkCommitMessage(commits[4], {present: true})
    await changeLogPage.checkCommitMessage(commits[3], {present: true})
    await changeLogPage.checkCommitMessage(commits[2], {present: true})
    await changeLogPage.checkCommitMessage(commits[1], {present: true})

    // ... some not
    await changeLogPage.checkCommitMessage(commits[0], {present: false})

    // Checks build attached to commits
    await changeLogPage.checkCommitBuild(commits[4], mockSCMContext, to, {expected: true})
    await changeLogPage.checkCommitBuild(commits[3], mockSCMContext, builds[2], {expected: true})
    await changeLogPage.checkCommitBuild(commits[2], mockSCMContext, builds[1], {expected: true})
    await changeLogPage.checkCommitBuild(commits[1], mockSCMContext, builds[1], {expected: false})

    /**
     * Issues
     */

    // Expecting the issues to be displayed
    for (const key of Object.keys(issues)) {
        const {summary} = issues[key]
        await changeLogPage.checkIssue({key, summary, visible: (key !== "ISS-20")})
    }

    // Returning the change log page for more tests
    return changeLogPage
}


test("SCM change log", async ({page, ontrack}) => {
    await doTestSCMChangeLog(page, ontrack)
})

test('JIRA SCM change log', async ({page, context}) => {
    await context.grantPermissions(['clipboard-read'])
    // Creates the JIRA mock configuration
    const configName = generate("mock-")
    await ontrack().configurations.jira.createConfig({
        name: configName,
        url: "mock://jira",
        user: "",
        password: "",
    })

    // Running the test
    const changeLogPage = await doTestSCMChangeLog(
        page,
        'jira',
        `jira//${configName}`
    )

    /**
     * Exporting the change log with default parameters
     */

    await changeLogPage.launchExport()
    await changeLogPage.checkExportedContent(
        trimIndent(
            `
                * ISS-21 Some new feature
                * ISS-22 Some fixes are needed
                * ISS-23 Some nicer UI
            `
        )
    )

    /**
     * Exporting the change log for Markdown and default parameters
     */

    await changeLogPage.selectExportFormat('Markdown')
    await changeLogPage.launchExport()
    await changeLogPage.checkExportedContent(
        trimIndent(
            `
                * [ISS-21](mock://jira/ISS/ISS-21) Some new feature
                * [ISS-22](mock://jira/ISS/ISS-22) Some fixes are needed
                * [ISS-23](mock://jira/ISS/ISS-23) Some nicer UI
            `
        )
    )

    /**
     * Exporting the change log for Markdown and grouping parameters
     */

    await changeLogPage.selectExportOptions({
        format: 'Markdown',
        groups: [
            {
                group: "Features",
                types: [
                    "feature",
                    "enhancement",
                ]
            },
            {
                group: "Fixes",
                types: [
                    "defect",
                ]
            },
        ]
    })
    await changeLogPage.launchExport()
    await changeLogPage.checkExportedContent(
        trimIndent(
            `
                ## Features
                
                * [ISS-21](mock://jira/ISS/ISS-21) Some new feature
                * [ISS-23](mock://jira/ISS/ISS-23) Some nicer UI
                
                ## Fixes
                
                * [ISS-22](mock://jira/ISS/ISS-22) Some fixes are needed
            `
        )
    )
})
