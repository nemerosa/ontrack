const {test} = require("../../fixtures/connection");
const {login} = require("../../core/login");
const {PromotionRunPage} = require("../../core/promotionRuns/PromotionRunPage");

test('promotion notification output contains a link to the promotion run', async ({page, ontrack}) => {
    // Target project to promote
    const targetProject = await ontrack.createProject()
    const targetBranch = await targetProject.createBranch()
    const targetPromotionLevel = await targetBranch.createPromotionLevel()
    const targetBuild = await targetBranch.createBuild()

    // Source project triggering the promotion
    const sourceProject = await ontrack.createProject()
    const sourceBranch = await sourceProject.createBranch()
    const sourcePromotionLevel = await sourceBranch.createPromotionLevel()
    const sourceBuild = await sourceBranch.createBuild()

    // Source promotion subscription to create a promotion in the target project
    await sourcePromotionLevel.subscribe({
        name: 'promotion-subscription',
        events: ['new_promotion_run'],
        channel: 'yontrack-promotion',
        channelConfig: {
            project: targetProject.name,
            branch: targetBranch.name,
            build: targetBuild.name,
            promotion: targetPromotionLevel.name,
            // No sync
        }
    })

    // Source promotion --> target promotion
    const sourcePromotionRun = await sourceBuild.promote(sourcePromotionLevel)

    // Going to the source promotion run page
    await login(page, ontrack)
    const sourcePromotionRunPage = new PromotionRunPage(page, sourcePromotionRun)
    await sourcePromotionRunPage.goTo()

    // Getting the notifications section
    const notificationsTable = await sourcePromotionRunPage.getNotificationsTable()

    // Getting the first notification for the promotion
    const notificationDetails = await notificationsTable.displayFirstNotificationForChannel('yontrack-promotion')
    await notificationDetails.details.getByRole('link', {name: targetPromotionLevel.name, exact: false}).click()

    // We expect to be on the target promotion run page
    const targetPromotionRuns = await targetBuild.promotionRuns(targetPromotionLevel)
    const targetPromotionRun = targetPromotionRuns[0]
    const targetPromotionRunPage = new PromotionRunPage(page, targetPromotionRun)
    await targetPromotionRunPage.expectOnPage()
})