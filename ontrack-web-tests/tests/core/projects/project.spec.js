const {expect} = require("@playwright/test");
const {login} = require("../login");
const {HomePage} = require("../home/home");
const {generate} = require("@ontrack/utils");
const {ProjectPage} = require("./project");
const {test} = require("../../fixtures/connection");
const {waitUntilCondition} = require("../../support/timing");

test('project creation', async ({page, ontrack}) => {
    await login(page, ontrack)

    const homePage = new HomePage(page, ontrack)
    const projectName = generate("p-")
    await homePage.newProject({name: projectName})

    await expect(page.getByText(projectName)).toBeVisible()
})

test('project disabling and enabling', async ({page, ontrack}) => {
    let project = await ontrack.createProject()

    await login(page, ontrack)
    const projectPage = new ProjectPage(page, ontrack, project)
    await projectPage.goTo()

    // Checking that the project is correctly enabled (using the API)
    project = await ontrack.getProjectById(project.id);
    expect(project.disabled).toBeFalsy()

    // Checking that there is NO banner showing that the project is disabled
    await projectPage.checkNoDisabledBanner()

    // Disabling the project
    await projectPage.disableProject()

    // Checking that the project is correctly disabled (using the API)
    await waitUntilCondition({
        page,
        condition: async () => {
            const p = await ontrack.getProjectById(project.id)
            return p.disabled
        },
        message: `Project ${project.name} is disabled`
    })

    // Checking that there IS a banner showing that the project is disabled
    await projectPage.checkDisabledBanner()

    // Enabling the project again
    await projectPage.enableProject()

    // Checking that the project is correctly enabled (using the API)
    await waitUntilCondition({
        page,
        condition: async () => {
            const p = await ontrack.getProjectById(project.id)
            return !p.disabled
        },
        message: `Project ${project.name} is enabled`
    })

    // Checking that there is NO banner showing that the project is disabled
    await projectPage.checkNoDisabledBanner()
})

test('deleting a project', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    // Login
    await login(page, ontrack)
    // Navigating to the project
    const projectPage = new ProjectPage(page, ontrack, project)
    await projectPage.goTo()

    // Deleting the project
    await projectPage.deleteProject()

    // Checking we are on the home page
    const homePage = new HomePage(page, ontrack)
    await homePage.checkOnPage()
})

