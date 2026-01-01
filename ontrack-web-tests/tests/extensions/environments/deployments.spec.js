import {test} from "../../fixtures/connection";
import {createSlot} from "./slotFixtures";
import {login} from "../../core/login";
import {BuildPage} from "../../core/builds/BuildPage";
import {getBuildEnvironmentSection} from "./BuildEnvironmentSection";

test('starting a deployment from the build page', async ({page, ontrack}) => {
    const {environment, project} = await createSlot(ontrack)

    const branch = await project.createBranch()
    const build = await branch.createBuild()

    // Going to the build page
    await login(page, ontrack)
    const buildPage = new BuildPage(page, build)
    await buildPage.goTo()

    // Gets the build environment section
    const buildEnvironmentSection = await getBuildEnvironmentSection(page, build)

    // Expecting a deployment button for this build & environment
    await buildEnvironmentSection.expectBuildDeployButton({environment})

    // Launching the deployment & expecting to be on a pipeline page
    await buildEnvironmentSection.buildDeploy({environment})
})
