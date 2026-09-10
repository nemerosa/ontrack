import {expect} from "@playwright/test";
import {createSlot} from "./slotFixtures";
import {login} from "../../core/login";
import {BranchDeliveryMapPage} from "../../core/branches/branchDeliveryMap";
import {test} from "../../fixtures/connection";
import {addSlotWorkflow} from "@ontrack/extensions/environments/workflows";

/**
 * A one-node workflow: what the map draws is the workflow's name, its trigger and its run, never
 * the shape of its graph.
 */
const slotWorkflowYaml = (name) => `
    name: ${name}
    nodes:
      - id: check
        executorId: mock
        data:
            text: Checking
`

/**
 * The slot half of the delivery map.
 *
 * On the environments side rather than beside the other delivery map specs, for the reason
 * `branchPipelineDecorations.spec.js` gives: the view itself is core and names no extension, so what
 * is checked here is the extension's end of the contributor seam. Core stays testable without
 * environments installed.
 *
 * The unresolved checkpoint is the map's headline value and gets a browser test of its own: an
 * admission rule naming a promotion which does not exist is saved happily, looks healthy on the
 * slot's own page, and is first heard of when a deployment refuses. The map is the one place it is
 * visible, and this is the test that says so end to end.
 */

const branchWithASlot = async (ontrack, {promotion}) => {
    const {project, slot} = await createSlot(ontrack)
    const branch = await project.createBranch()
    await ontrack.environments.addAdmissionRule({
        slot,
        ruleId: "promotion",
        ruleConfig: {promotion},
    })
    return {project, branch, slot}
}

test('a slot is drawn on the map, joined to the promotion its rule names', async ({page, ontrack}) => {
    const {branch, slot} = await branchWithASlot(ontrack, {promotion: "SILVER"})
    const silver = await branch.createPromotionLevel("SILVER")

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    await expect(map.slotCheckpoint(slot)).toBeVisible()
    await expect(map.promotionCheckpoint(silver)).toBeVisible()

    // The rule CONSTRAINS - the slot cannot be reached until SILVER has been - and the label is read
    // along the arrow, which runs from the prerequisite
    await map.checkEdgeCount("required by", 1)
})

test('a rule naming a promotion this branch does not have is drawn, not dropped', async ({page, ontrack}) => {
    // The branch declares BRONZE and never SILVER, which is how the mistake is actually made: the
    // rule was written for a project which has one
    const {branch, slot} = await branchWithASlot(ontrack, {promotion: "SILVER"})
    await branch.createPromotionLevel("BRONZE")

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    const unresolved = map.unresolvedCheckpoint('promotion-level', "SILVER")
    await expect(unresolved).toBeVisible()
    // It carries the name the rule asked for, and says what failed to match it
    await expect(unresolved).toContainText("SILVER")
    await expect(unresolved).toContainText("No such promotion level")

    // The line is still drawn: the configuration is real, and it is the reason the slot cannot be
    // reached
    await expect(map.slotCheckpoint(slot)).toBeVisible()
    await map.checkEdgeCount("required by", 1)

    // Never mistakable for something hidden by permissions, which is left out of the map entirely
    await expect(unresolved).not.toContainText(/permission/i)
})

test('a slot with workflows on two triggers straddles its own column', async ({page, ontrack}) => {
    const {project, slot} = await createSlot(ontrack)
    const branch = await project.createBranch()

    // A CANDIDATE workflow is a hard gate - the deployment cannot start until it passes - while a
    // DONE one runs once the deployment is over and nothing waits for it
    const gate = await addSlotWorkflow({
        slot,
        trigger: 'CANDIDATE',
        workflowYaml: slotWorkflowYaml("Readiness check"),
    })
    const consequence = await addSlotWorkflow({
        slot,
        trigger: 'DONE',
        workflowYaml: slotWorkflowYaml("Deployment announcement"),
    })

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    await expect(map.slotWorkflowCheckpoint(gate)).toBeVisible()
    await expect(map.slotWorkflowCheckpoint(consequence)).toBeVisible()

    // Each names the moment of the deployment which fires it
    await expect(map.slotWorkflowCheckpoint(gate).getByTestId('slot-workflow-trigger'))
        .toHaveText("on candidate")
    await expect(map.slotWorkflowCheckpoint(consequence).getByTestId('slot-workflow-trigger'))
        .toHaveText("on done")

    // The gate runs INTO the slot as a prerequisite; the consequence runs OUT of it
    await map.checkEdgeCount("required by", 1)
    await map.checkEdgeCount("emits", 1)
})

test('a slot workflow which has never run is drawn, and so is its line', async ({page, ontrack}) => {
    // A gate nobody ever ran is not dormant: it is very often the reason nothing has ever been
    // deployed to that slot, and saying so is the most useful thing the map can offer about it
    const {project, slot} = await createSlot(ontrack)
    const branch = await project.createBranch()
    const gate = await addSlotWorkflow({
        slot,
        trigger: 'CANDIDATE',
        workflowYaml: slotWorkflowYaml("Readiness check"),
    })

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    const checkpoint = map.slotWorkflowCheckpoint(gate)
    await expect(checkpoint).toContainText("Readiness check")
    await expect(checkpoint).toContainText("Not started")
    await map.checkEdgeCount("required by", 1)
})

test('the workflows toggle takes the slot workflows off the map too', async ({page, ontrack}) => {
    // ONE entry for both kinds: a reader who wants workflows out of the way wants all of them out
    const {project, slot} = await createSlot(ontrack)
    const branch = await project.createBranch()
    const gate = await addSlotWorkflow({
        slot,
        trigger: 'CANDIDATE',
        workflowYaml: slotWorkflowYaml("Readiness check"),
    })

    await login(page, ontrack)
    const map = new BranchDeliveryMapPage(page, branch)
    await map.goTo()

    await expect(map.slotWorkflowCheckpoint(gate)).toBeVisible()
    await map.toggleWorkflows()
    await expect(map.slotWorkflowCheckpoint(gate)).toBeHidden()
    // The slot itself stays: a statement about workflows is not a statement about slots
    await expect(map.slotCheckpoint(slot)).toBeVisible()
})
