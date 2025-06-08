import {BranchPage} from "../branches/branch";
import {login} from "../login";
import {test} from "../../fixtures/connection";
import {PromotionLevelPage} from "./PromotionLevelPage";
import path from "node:path";

test('uploading and getting the image for a promotion level', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const promotionLevel = await branch.createPromotionLevel("GOLD")

    await login(page, ontrack)

    const promotionLevelPage = new PromotionLevelPage(page, promotionLevel)
    await promotionLevelPage.goTo()

    await promotionLevelPage.changeImage(path.join(__dirname, 'gold.png'))

    await promotionLevelPage.checkImage()
})

test('promotion level description is not required', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()

    await login(page, ontrack)

    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    const promotionsPage = await branchPage.navigateToPromotions()
    await promotionsPage.createPromotionLevel({name: "GOLD"})

    await promotionsPage.checkPromotionLevel({name: "GOLD"})
})