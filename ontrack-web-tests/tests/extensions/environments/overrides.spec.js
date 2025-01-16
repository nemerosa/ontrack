import {test} from "@playwright/test";
import {createPipelineWithPromotionRule} from "./admissionRulesFixtures";
import {login} from "../../core/login";
import {PipelinePage} from "./PipelinePage";

test('overriding a rule', async ({page}) => {
    const {pipeline, ruleConfigId} = await createPipelineWithPromotionRule({})

    await login(page)

    const pipelinePage = new PipelinePage(page, pipeline)
    await pipelinePage.goTo()

    // We cannot deploy because build is not promoted
    await pipelinePage.checkRunAction({disabled: true})

    // Overriding the rule
    const pipelineRule = await pipelinePage.getAdmissionRule(ruleConfigId)
    await pipelineRule.checkRuleOverridden({overridden: false})
    await pipelineRule.checkOverrideRuleButton({visible: true})

    await pipelineRule.overrideRule({message: "We cannot wait"})
    await pipelineRule.checkRuleOverridden({overridden: true})
    await pipelineRule.checkOverrideRuleButton({visible: false})

    // We can now deploy because the rule has been overridden
    await pipelinePage.checkRunAction({disabled: false})
    await pipelinePage.expectRuleStatusProgress({present: true, value: 100, overridden: true})
})
