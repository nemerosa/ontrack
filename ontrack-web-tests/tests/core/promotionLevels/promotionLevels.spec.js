import {test} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";
import {BranchPage} from "../branches/branch";
import {login} from "../login";

test('promotion level description is not required', async ({page}) => {
    const project = await ontrack().createProject()
    const branch = await project.createBranch()

    await login(page)

    const branchPage = new BranchPage(page, branch)
    await branchPage.goTo()

    const promotionsPage = await branchPage.navigateToPromotions()
    await promotionsPage.createPromotionLevel({name: "GOLD"})

    await promotionsPage.checkPromotionLevel({name: "GOLD"})
})