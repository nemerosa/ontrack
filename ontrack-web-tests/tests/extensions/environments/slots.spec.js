import {test} from "@playwright/test";
import {login} from "../../core/login";
import {HomePage} from "../../core/home/home";
import {ontrack} from "@ontrack/ontrack";
import {createSlot} from "./slotFixtures";
import {SlotPage} from "./SlotPage";
import {EnvironmentsPage} from "./Environments";

test('creating a slot for several environments', async ({page}) => {

    // Provisioning
    // Creating two environments
    const env1 = await ontrack().environments.createEnvironment({})
    const env2 = await ontrack().environments.createEnvironment({})
    // Creating a project
    const project = await ontrack().createProject()

    // Login
    await login(page)
    // Going to the environment page, using the button in the home page
    const homePage = new HomePage(page)
    const environmentsPage = await homePage.selectEnvironments()

    // Creating a new slot for the 2 environments
    await environmentsPage.createSlot({
        projectName: project.name,
        qualifier: '',
        description: "Slot",
        environmentNames: [env1.name, env2.name],
    })
    // Checks that the slot card is visible for each environment
    await environmentsPage.checkSlotIsVisible(env1, project.name)
    await environmentsPage.checkSlotIsVisible(env2, project.name)
})

test('deleting a slot', async ({page}) => {
    const {slot} = await createSlot(ontrack())

    // Login
    await login(page)
    // Going to the slot page
    const slotPage = new SlotPage(page, slot)
    await slotPage.goTo()

    // Deleting the slot
    await slotPage.delete()

    // We're back in the environment page
    const environmentsPage = new EnvironmentsPage(page)
    await environmentsPage.checkEnvironmentIsVisible(slot.environment.name)
})

test('eligible and deployable builds for a slot', async ({page}) => {
    const {project, slot} = await createSlot(ontrack())
    // Promotion admission rule
    await ontrack().environments.addPromotionRule({slot, promotion: "BRONZE"})
    // Branch for the project
    const branch = await project.createBranch()
    const bronze = await branch.createPromotionLevel("BRONZE")
    // Build not promoted
    const build1 = await branch.createBuild()
    // Build, promoted
    const build2 = await branch.createBuild()
    await build2.promote(bronze)

    // Login & going to the slot page
    await login(page)
    const slotPage = new SlotPage(page, slot)
    await slotPage.goTo()

    // Gets the section about builds
    const slotBuilds = await slotPage.getSlotBuilds()
    await slotBuilds.checkBuildPresent(build2)
    await slotBuilds.checkBuildNotPresent(build1)
    // Selecting all eligible builds
    await slotBuilds.selectAllBuilds()
    await slotBuilds.checkBuildPresent(build2)
    await slotBuilds.checkBuildPresent(build1)
})
