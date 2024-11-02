import {test} from "@playwright/test";
import {createPipelineWithPromotionRule} from "./admissionRulesFixtures";
import {login} from "../../core/login";
import {PipelinePage} from "./PipelinePage";

test('overriding a rule', async ({page}) => {
    const {pipeline} = await createPipelineWithPromotionRule({})

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    const pipelineActions = await pipelinePage.checkPipelineActions()
    // We cannot deploy because build is not promoted
    await pipelineActions.checkDeployingAction({visible: false})

    await pipelinePage.checkRuleOverridden({name: "promotion", overridden: false})
    await pipelinePage.checkOverrideRuleButton({name: "promotion", visible: true})

    await pipelinePage.overrideRule({name: "promotion", message: "We cannot wait"})
    await pipelinePage.checkRuleOverridden({name: "promotion", overridden: true})

    // We can now deploy because the rule has been overridden
    await pipelineActions.checkDeployingAction({visible: true})
    await pipelineActions.expectStatusProgress({present: true, value: 100, overridden: true})
})

// TODO Cancelling override