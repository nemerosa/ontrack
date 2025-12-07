const {test} = require("../../fixtures/connection");
const {setupSimpleAutoVersioning} = require("./auto-versioning");
const {expect} = require("@playwright/test");
const {login} = require("../../core/login");
const {AutoVersioningAuditPage} = require("./AutoVersioningAuditPage");
const {AutoVersioningAuditDetailsPage} = require("./AutoVersioningAuditDetailsPage");

test('rescheduling an auto-versioning order', async ({page, ontrack}) => {
    // Auto-versioning context
    const autoVersioning = await setupSimpleAutoVersioning({page, ontrack})
    // Launching a promotion, triggering an auto-versioning request
    const depBuild = await autoVersioning.createDepBuild()
    await autoVersioning.promoteDepBuild(depBuild)
    const entry = await autoVersioning.waitForAutoVersioningCompletion({depBuild})
    await expect(entry.mostRecentState.state).toBe('PR_MERGED')

    // Checking the audit page
    await login(page, ontrack)
    const avAuditPage = new AutoVersioningAuditPage(page, ontrack)
    await avAuditPage.goTo()

    // Check that the initial order is present in the page and in the correct state
    const entryRow = await avAuditPage.getEntryRow(entry.order.uuid)
    await entryRow.checkState('PR merged')

    // Going to the details page
    const avDetailsPage = await entryRow.showDetails()

    // On the details page, rescheduling the order
    await avDetailsPage.reschedule()

    // Getting the new entry, which must be different
    const rescheduledEntry = await autoVersioning.waitForAutoVersioningCompletion({depBuild, state: 'PROCESSING_ABORTED'})
    await expect(rescheduledEntry.order.uuid).not.toBe(entry.order.uuid)

    // It must be aborted (same version)
    await expect(rescheduledEntry.mostRecentState.state).toBe('PROCESSING_ABORTED')

    // We must be on the new entry page
    const rescheduledDetailsPage = new AutoVersioningAuditDetailsPage(page, ontrack, rescheduledEntry.order.uuid)
    await rescheduledDetailsPage.expectOnPage()

    // We expect the entry to be aborted (after some time)
    await rescheduledDetailsPage.waitState('Aborted')
})
