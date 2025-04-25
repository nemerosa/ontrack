import {login} from "../login";
import {BuildPage} from "../builds/build";
import {test} from "../../fixtures/connection";

test('repromoting a promotion run', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const pl = await branch.createPromotionLevel()
    const build = await branch.createBuild()
    const run = await build.promote(pl)

    // Going to the build page
    await login(page, ontrack)
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()

    // Management of promotions
    const promotionInfoSection = await buildPage.getPromotionInfoSection()

    // Repromoting the build
    await promotionInfoSection.repromote(run)

    // Checking that we have two promotions now
    await promotionInfoSection.checkPromotionRunCount(pl, 2)
})
