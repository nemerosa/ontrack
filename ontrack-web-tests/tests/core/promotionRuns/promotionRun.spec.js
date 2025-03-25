import {test} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";
import {login} from "../login";
import {BuildPage} from "../builds/build";

test('repromoting a promotion run', async ({page}) => {
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const pl = await branch.createPromotionLevel()
    const build = await branch.createBuild()
    const run = await build.promote(pl)

    // Going to the build page
    await login(page)
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()

    // Management of promotions
    const promotionInfoSection = await buildPage.getPromotionInfoSection()

    // Repromoting the build
    await promotionInfoSection.repromote(run)

    // Checking that we have two promotions now
    await promotionInfoSection.checkPromotionRunCount(pl, 2)
})
