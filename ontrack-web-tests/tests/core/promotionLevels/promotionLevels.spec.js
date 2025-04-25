import {BranchPage} from "../branches/branch";
import {login} from "../login";
import {test} from "../../fixtures/connection";

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