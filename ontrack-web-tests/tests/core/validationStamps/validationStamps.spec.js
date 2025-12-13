import {login} from "../login";
import {test} from "../../fixtures/connection";
import path from "node:path";
import {ValidationStampPage} from "../validationRuns/validationStamp";

test('uploading and getting the image for a validation stamp', async ({page, ontrack}) => {
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp("helm")

    await login(page, ontrack)

    const validationStampPage = new ValidationStampPage(page, validationStamp)
    await validationStampPage.goTo()

    await validationStampPage.changeImage(path.join(__dirname, 'helm.png'))

    await validationStampPage.checkImage()
})
