import {login} from "../login";
import {BuildPage} from "../builds/BuildPage";
import {test} from "../../fixtures/connection";
import {PromotionRunPage} from "./PromotionRunPage";

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

test('deleting a promotion run from the promotion run page', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const pl = await branch.createPromotionLevel()
    const build = await branch.createBuild()
    const run = await build.promote(pl)

    // Going to the promotion run page
    await login(page, ontrack)
    const promotionRunPage = new PromotionRunPage(page, run)
    await promotionRunPage.goTo()

    // Deleting the promotion run
    await promotionRunPage.deletePromotionRun()

    // Checking we are back on the build page
    const buildPage = new BuildPage(page, build)
    await buildPage.checkOnBuildPage()
})

test('deleting a promotion run from the build promotions widget', async ({page, ontrack}) => {
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

    // Checks that we have one promotion
    await promotionInfoSection.checkPromotionRunCount(pl, 1)

    // Repromoting the build
    await promotionInfoSection.deletePromotionRun(run)

    // Checks that we have no promotion any longer
    await promotionInfoSection.checkPromotionRunCount(pl, 0)
})
