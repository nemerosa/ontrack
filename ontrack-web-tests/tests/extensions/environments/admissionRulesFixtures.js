import {createSlot} from "./slotFixtures";
import {ontrack} from "@ontrack/ontrack";

export const createPipelineWithPromotionRule = async ({promotion = "GOLD"}) => {
    const {project, slot} = await createSlot(ontrack())
    const ruleConfigId = await ontrack().environments.addPromotionRule({slot, promotion})

    const branch = await project.createBranch()
    const build = await branch.createBuild()

    await branch.createPromotionLevel(promotion)

    const pipeline = await slot.createPipeline({build})

    return {
        project,
        slot,
        pipeline,
        ruleConfigId,
    }
}