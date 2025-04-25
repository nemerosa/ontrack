import {createPipelineWithPromotionRule} from "./admissionRulesFixtures";
import {login} from "../../core/login";
import {PipelinePage} from "./PipelinePage";
import {test} from "../../fixtures/connection";

test('overriding a rule', async ({page, ontrack}) => {
    const {pipeline, ruleConfigId} = await createPipelineWithPromotionRule(ontrack, {})

    await login(page, ontrack)

    const pipelinePage = new PipelinePage(page, pipeline, ontrack)
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
