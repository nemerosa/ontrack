const {test} = require("../../fixtures/connection");
const {setupSimpleAutoVersioning} = require("./auto-versioning");
const {expect} = require("@playwright/test");
const {login} = require("../../core/login");
const {BranchPage} = require("../../core/branches/branch");
const {getBranchById} = require("@ontrack/branch");

test('loading the statuses of the PRs in the branch links graph', async ({page, ontrack}) => {
    const {depBuild, entry, targetBuild, branchLinksPage} = await prepareBranchLinks(page, ontrack);

    // Checking that no PR status is visible
    let linkNode = await branchLinksPage.getLinkNode(targetBuild.branch.project.name, depBuild.branch.project.name)
    await linkNode.expectPRLink(entry.mostRecentState.data.prName)
    await linkNode.expectNoPRStatus(entry.mostRecentState.data.prName)

    // Loading the PR statuses
    await branchLinksPage.loadPRStatuses()

    // Checking that the PR status is visible
    linkNode = await branchLinksPage.getLinkNode(targetBuild.branch.project.name, depBuild.branch.project.name)
    await linkNode.expectPRLink(entry.mostRecentState.data.prName)
    await linkNode.expectPRStatus(entry.mostRecentState.data.prName, "Merged")
})

test('loading the statuses of the PRs in the branch links table', async ({page, ontrack}) => {
    const {depBuild, entry, targetBuild, branchLinksPage} = await prepareBranchLinks(page, ontrack);

    // Displaying the table instead
    const branchLinksTablePage = await branchLinksPage.displayAsTable()

    // Checking that no PR status is visible
    let autoVersioningCell = await branchLinksTablePage.getAutoVersioningCell()
    await autoVersioningCell.expectPRLink(entry.mostRecentState.data.prName)
    await autoVersioningCell.expectNoPRStatus(entry.mostRecentState.data.prName)

    // Loading the PR statuses
    await branchLinksTablePage.loadPRStatuses()

    // Checking that the PR status is visible
    autoVersioningCell = await branchLinksTablePage.getAutoVersioningCell()
    await autoVersioningCell.expectPRLink(entry.mostRecentState.data.prName)
    await autoVersioningCell.expectPRStatus(entry.mostRecentState.data.prName, "Merged")
})

async function prepareBranchLinks(page, ontrack) {
    // Auto-versioning context
    const autoVersioning = await setupSimpleAutoVersioning({page, ontrack})
    // Launching a promotion, triggering an auto-versioning request
    const depBuild = await autoVersioning.createDepBuild()
    await autoVersioning.promoteDepBuild(depBuild)
    const entry = await autoVersioning.waitForAutoVersioningCompletion({depBuild})
    await expect(entry.mostRecentState.state).toBe('PR_MERGED')

    // Creating the links with "AV check"
    const targetBuild = await autoVersioning.createTargetBuild()
    await targetBuild.autoVersioningCheck()

    // Going to the target branch page & displays the links graph
    const branch = await getBranchById(ontrack, entry.order.branch.id)
    await login(page, ontrack)
    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    // Clicking on the "Links"
    const branchLinksPage = await branchPage.displayBranchLinks()
    return {depBuild, entry, targetBuild, branchLinksPage};
}
