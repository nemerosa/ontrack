const {test, expect} = require('@playwright/test');
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {generate} = require("@ontrack/utils");
const {PromotionRunPage} = require("./PromotionRunPage");

test('notifications are visible on the promotion run page', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const pl = await branch.createPromotionLevel()
    // Provisioning of the subscription
    const group = generate("pl-group-")
    await pl.subscribe({
        name: `Subscription ${group}`,
        events: ["new_promotion_run"],
        channel: "in-memory",
        channelConfig: {
            group: group,
        }
    })
    // Promotion
    const build = await branch.createBuild()
    const run = await build.promote(pl)
    // Login
    await login(page)
    // Navigating to the promotion run
    const runPage = new PromotionRunPage(page, run)
    await runPage.goTo()
    // Checking that the notification is visible
    await runPage.assertNotificationPresent(`Subscription ${group} @ ${pl.name} | ${branch.name} / ${project.name}`)
})
