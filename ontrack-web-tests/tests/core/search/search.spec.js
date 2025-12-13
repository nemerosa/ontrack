const {test} = require("../../fixtures/connection");
const {login} = require("../login");
const {HomePage} = require("../home/home");
const {ProjectPage} = require("../projects/project");

test('searching for a project', async ({page, ontrack}) => {
    await login(page, ontrack)

    const project = await ontrack.createProject()

    const homePage = new HomePage(page, ontrack)
    const searchPage = await homePage.search(project.name)

    await searchPage.expectProjectResultPresent(project.name)

    await searchPage.clickProjectResult(project.name)

    const projectPage = new ProjectPage(page, ontrack, project)
    await projectPage.expectOnPage()
})
