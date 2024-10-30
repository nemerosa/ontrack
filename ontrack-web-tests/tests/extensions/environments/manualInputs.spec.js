import {test} from "@playwright/test";
import {ontrack} from "@ontrack/ontrack";
import {createSlot} from "./Slots";
import {login} from "../../core/login";
import {EnvironmentsPage} from "./Environments";

test('manual approval on the environments page', async ({page}) => {
    const {project, slot} = await createSlot(ontrack())
    await ontrack().environments.addManualApproval({slot})

    const branch = await project.createBranch()
    const build = await branch.createBuild()

    const pipeline = await slot.createPipeline({build})

    await login(page)

    const environmentsPage = new EnvironmentsPage(page)
    await environmentsPage.goTo()

    const pipelineCard = await environmentsPage.checkPipelineCard(pipeline)
    await pipelineCard.expectManualInputButton()

    await pipelineCard.manualInput({
        actions: (dialog) => {
            dialog.getByTestId('manual_approval').click()
            dialog.getByTestId('manual_message').fill("OK for me")
        }
    })

    await pipelineCard.expectManualInputButton(false)
})
