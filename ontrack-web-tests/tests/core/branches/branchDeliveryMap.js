const {expect} = require("@playwright/test");

/**
 * The delivery map branch content view.
 *
 * A page object of its own, beside `BranchPage` and `BranchPipelinePage`, for the reason the pipeline
 * one gives: three views answer the same questions with different regions, and one object holding all
 * of their vocabularies would be three objects pretending to be one.
 *
 * Checkpoints are addressed by the ids the SERVER builds - `promotion-level:12`, `slot:<uuid>` - which
 * is what the frontend puts in each node's test id. Rebuilding them here rather than reading them off
 * the page is deliberate: a test which finds a node by its label would pass just as happily against a
 * map that drew the wrong entity under the right name.
 */
class BranchDeliveryMapPage {

    constructor(page, branch) {
        this.page = page
        this.branch = branch
    }

    async goTo() {
        await this.page.goto(`${this.branch.ontrack.connection.ui}/branch/${this.branch.id}?view=delivery-map`)
        await this.checkOnPage()
    }

    async checkOnPage() {
        // Scoped to the page header: after a client-side navigation Next mirrors the document title,
        // which carries the branch name, into its route announcer
        await expect(this.page.getByTestId('branch-title')).toContainText(this.branch.name)
        await expect(this.page.getByTestId('delivery-map-header')).toBeVisible()
    }

    // --- Checkpoints --------------------------------------------------------

    checkpoint(id) {
        return this.page.getByTestId(`checkpoint-${id}`)
    }

    promotionCheckpoint(promotionLevel) {
        return this.checkpoint(`promotion-level:${promotionLevel.id}`)
    }

    validationStampCheckpoint(validationStamp) {
        return this.checkpoint(`validation-stamp:${validationStamp.id}`)
    }

    slotCheckpoint(slot) {
        return this.checkpoint(`slot:${slot.id}`)
    }

    /**
     * A workflow a promotion set off. Its id is built from the promotion level and the workflow's
     * NAME - there is no configured object behind it the map can key on, which is why two workflows
     * of one promotion sharing a name collapse into one checkpoint.
     */
    workflowCheckpoint(promotionLevel, workflowName) {
        return this.checkpoint(`workflow:${promotionLevel.id}:${workflowName}`)
    }

    /**
     * A workflow configured on a slot, keyed on the CONFIGURATION rather than on any run: a
     * per-run id would change the map's topology on every deployment.
     */
    slotWorkflowCheckpoint(slotWorkflow) {
        return this.checkpoint(`slot-workflow:${slotWorkflow.id}`)
    }

    /**
     * The checkpoint a configuration asked for and which matches nothing. Its id is built from what
     * was looked for and the name that was asked for, and from nothing else.
     */
    unresolvedCheckpoint(reference, name) {
        return this.checkpoint(`unresolved:${reference}:${name}`)
    }

    async checkCheckpointNames(checkpoint, build) {
        await expect(checkpoint).toContainText(build.name)
    }

    /**
     * How far behind the branch head the checkpoint's build is: "at head", or "3 behind".
     */
    async checkCheckpointLag(checkpoint, lag) {
        await expect(checkpoint.getByTestId('checkpoint-lag')).toHaveText(lag)
    }

    async checkCheckpointHasNoLag(checkpoint) {
        await expect(checkpoint.getByTestId('checkpoint-lag')).toBeHidden()
    }

    // --- Edges --------------------------------------------------------------

    /**
     * Edge labels are read ALONG the arrow, so the constraining kind reads "required by" rather than
     * "requires" - see `CONTEXT.md`.
     */
    edgeLabels(label) {
        return this.page.getByTestId('delivery-map-graph').getByText(label, {exact: true})
    }

    async checkEdgeCount(label, count) {
        await expect(this.edgeLabels(label)).toHaveCount(count)
    }

    // --- Header and toolbar -------------------------------------------------

    async checkLatestBuild(build) {
        await expect(this.page.getByTestId('delivery-map-header')).toContainText(build.name)
    }

    async checkNoBuildYet() {
        await expect(this.page.getByTestId('delivery-map-header')).toContainText("No build on this branch yet")
    }

    validationStampsToggle() {
        return this.page.getByTestId('delivery-map-show-validation-stamps')
    }

    async toggleValidationStamps() {
        await this.validationStampsToggle().click()
    }

    async checkValidationStampsShown(shown) {
        if (shown) {
            await expect(this.validationStampsToggle()).toBeChecked()
        } else {
            await expect(this.validationStampsToggle()).not.toBeChecked()
        }
    }

    /**
     * ONE toggle for both workflow kinds: a reader who wants workflows out of the way wants all of
     * them out of the way, whichever extension contributed them.
     */
    workflowsToggle() {
        return this.page.getByTestId('delivery-map-show-workflows')
    }

    async toggleWorkflows() {
        await this.workflowsToggle().click()
    }

    // --- Empty states -------------------------------------------------------

    async checkEmpty() {
        await expect(this.page.getByTestId('delivery-map-empty')).toBeVisible()
    }

    async checkNoDependenciesNotice() {
        await expect(this.page.getByTestId('delivery-map-no-dependencies')).toBeVisible()
    }

    async checkExperimentalAlert() {
        await expect(this.page.getByTestId('delivery-map-experimental-alert')).toBeVisible()
    }

}

module.exports = {BranchDeliveryMapPage}
