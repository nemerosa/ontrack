import {test} from "../../fixtures/connection";
import {login} from "../login";
import {generate} from "@ontrack/utils";
import {BranchLinksPage} from "./BranchLinksPage";

test('promotions in the branch node latest build are decorated with notification badges', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
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

    // Login & going to the branch links page
    await login(page, ontrack)
    const branchLinksPage = new BranchLinksPage(page, ontrack, branch)
    await branchLinksPage.goTo()

    // Getting the branch node
    const branchNode = await branchLinksPage.getBranchNode(project.name)
    await branchNode.expectPromotionRunWithBadge(run, {count: 1, type: 'success'})
});
