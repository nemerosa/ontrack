const {test, expect} = require("@playwright/test");
const {login} = require("../login");
const {HomePage} = require("../home/home");
const {generate} = require("@ontrack/utils");
const {ontrack} = require("@ontrack/ontrack");
const {ProjectPage} = require("./project");
const {BranchPage} = require("../branches/branch");

test('project creation', async ({page}) => {
    await login(page)

    const homePage = new HomePage(page)
    const projectName = generate("p-")
    await homePage.newProject({name: projectName})

    await expect(page.getByText(projectName)).toBeVisible()
})

test('project disabling and enabling', async ({page}) => {
    let project = await ontrack().createProject()

    await login(page)
    const projectPage = new ProjectPage(page, project)
    await projectPage.goTo()

    // Checking that the project is correctly enabled (using the API)
    project = await ontrack().getProjectById(project.id);
    expect(project.disabled).toBeFalsy()

    // Checking that there is NO banner showing that the project is disabled
    await projectPage.checkNoDisabledBanner()

    // Disabling the project
    await projectPage.disableProject()

    // Checking that the project is correctly disabled (using the API)
    project = await ontrack().getProjectById(project.id);
    expect(project.disabled).toBeTruthy()

    // Checking that there IS a banner showing that the project is disabled
    await projectPage.checkDisabledBanner()

    // Enabling the project again
    await projectPage.enableProject()

    // Checking that the project is correctly eabled (using the API)
    project = await ontrack().getProjectById(project.id);
    expect(project.disabled).toBeFalsy()

    // Checking that there is NO banner showing that the project is disabled
    await projectPage.checkNoDisabledBanner()
})

test('deleting a project', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    // Login
    await login(page)
    // Navigating to the project
    const projectPage = new ProjectPage(page, project)
    await projectPage.goTo()

    // Deleting the project
    await projectPage.deleteProject()

    // Checking we are on the home page
    const homePage = new HomePage(page)
    await homePage.checkOnPage()
})

