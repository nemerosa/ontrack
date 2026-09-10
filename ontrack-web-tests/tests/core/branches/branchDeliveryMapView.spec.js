const {expect} = require("@playwright/test");
const {login} = require("../login");
const {BranchDeliveryMapPage} = require("./branchDeliveryMap");
const {test} = require("../../fixtures/connection");

/**
 * The delivery map branch content view: what a build on this branch has to pass through on its way to
 * an environment.
 *
 * What is checked here is what only a browser can answer - that the configuration a project actually
 * carries is drawn, that a checkpoint names the build which reached it and says how far behind the
 * branch head that build is, that the toolbar's toggle takes the validation stamps off the map and is
 * remembered, and that a rule naming nothing is drawn rather than silently dropped. The narrowing
 * rules, the topology comparison behind the refresh, and every empty-state predicate are pinned by
 * the Jest tests, which cover them far faster.
 */

/**
 * A branch carrying one of each thing the map draws:
 *
 * - BRONZE unlocks SILVER, and so does the QUALITY stamp - auto promotion is the only thing which
 *   puts a validation stamp on the map at all;
 * - GOLD requires SILVER, which is the other edge kind;
 * - LONELY is a stamp no promotion depends on, and is therefore NOT on the map;
 * - two builds, so that the head and the lag markers have something to say.
 */
const branchWithADeliveryMap = async (ontrack) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()

    const bronze = await branch.createPromotionLevel("BRONZE")
    const silver = await branch.createPromotionLevel("SILVER")
    const gold = await branch.createPromotionLevel("GOLD")

    const quality = await branch.createValidationStamp("QUALITY")
    const lonely = await branch.createValidationStamp("LONELY")

    await silver.setAutoPromotionProperty({validationStamps: [quality], promotionLevels: [bronze]})
    await gold.setPromotionDependenciesProperty({dependencies: [silver]})

    // Oldest first: `promoted` is the build which reached everything, `head` the one after it
    const promoted = await branch.createBuild()
    await promoted.promote(bronze)
    await promoted.promote(silver)
    await promoted.validate(quality)

    const head = await branch.createBuild()

    return {branch, bronze, silver, gold, quality, lonely, promoted, head}
}

test('the delivery map draws the configuration, and only what takes part in it', async ({page, ontrack}) => {
    const {branch, bronze, silver, gold, quality, lonely} = await branchWithADeliveryMap(ontrack)

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    // Every promotion level is drawn, granted by something or not
    await expect(map.promotionCheckpoint(bronze)).toBeVisible()
    await expect(map.promotionCheckpoint(silver)).toBeVisible()
    await expect(map.promotionCheckpoint(gold)).toBeVisible()

    // A stamp is drawn only when a promotion depends on it
    await expect(map.validationStampCheckpoint(quality)).toBeVisible()
    await expect(map.validationStampCheckpoint(lonely)).toBeHidden()

    // Auto promotion ACTS - one edge from the stamp, one from the promotion it also names - while a
    // dependency only CONSTRAINS, and is labelled for the direction it is read in
    await map.checkEdgeCount("unlocks", 2)
    await map.checkEdgeCount("required by", 1)

    await map.checkExperimentalAlert()
})

test('a checkpoint names the build which reached it, and how far behind the head it is', async ({page, ontrack}) => {
    const {branch, silver, gold, quality, promoted, head} = await branchWithADeliveryMap(ontrack)

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    // The frame of reference, stated once in the header rather than drawn as a node
    await map.checkLatestBuild(head)

    // Everything reached was reached by the build before the head
    await map.checkCheckpointNames(map.promotionCheckpoint(silver), promoted)
    await map.checkCheckpointLag(map.promotionCheckpoint(silver), "1 behind")
    await map.checkCheckpointNames(map.validationStampCheckpoint(quality), promoted)
    await map.checkCheckpointLag(map.validationStampCheckpoint(quality), "1 behind")

    // Nothing has ever been promoted to GOLD, so it carries no build and no lag
    await expect(map.promotionCheckpoint(gold)).toContainText("Never promoted")
    await map.checkCheckpointHasNoLag(map.promotionCheckpoint(gold))
})

test('a checkpoint the branch head has reached says so', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const bronze = await branch.createPromotionLevel("BRONZE")
    const build = await branch.createBuild()
    await build.promote(bronze)

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    await map.checkLatestBuild(build)
    await map.checkCheckpointLag(map.promotionCheckpoint(bronze), "at head")
})

test('the validation stamps can be taken off the map, and stay off', async ({page, ontrack}) => {
    const {branch, silver, quality} = await branchWithADeliveryMap(ontrack)

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    // Shown by default: a map opening on a chain of promotions with no visible cause hides the very
    // thing which explains them
    await map.checkValidationStampsShown(true)
    await expect(map.validationStampCheckpoint(quality)).toBeVisible()

    await map.toggleValidationStamps()
    await expect(map.validationStampCheckpoint(quality)).toBeHidden()
    // The promotion levels stay: a statement about stamps is not a statement about promotions
    await expect(map.promotionCheckpoint(silver)).toBeVisible()
    // And the edges which ended on a stamp go with it
    await map.checkEdgeCount("unlocks", 1)

    // Remembered in the browser, which is the whole point of storing it at all
    await page.reload()
    await map.checkOnPage()
    await map.checkValidationStampsShown(false)
    await expect(map.validationStampCheckpoint(quality)).toBeHidden()
})

test('a branch with promotion levels and no configuration says what would join them', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const bronze = await branch.createPromotionLevel("BRONZE")
    await branch.createBuild()

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    // The promotion level is still drawn - it is what a build has to pass through whether or not
    // anything grants it yet - with a note saying which configuration draws the lines
    await expect(map.promotionCheckpoint(bronze)).toBeVisible()
    await map.checkNoDependenciesNotice()
})

test('a branch with nothing on its map says so instead of drawing an empty rectangle', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    await map.checkEmpty()
    await map.checkNoBuildYet()
})
