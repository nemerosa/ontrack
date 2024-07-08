const {test, expect} = require("@playwright/test");
const {login} = require("../login");
const {HomePage} = require("../home/home");
const {generate} = require("@ontrack/utils");

test('project creation', async ({page}) => {
    await login(page)

    const homePage = new HomePage(page)
    const projectName = generate("p-")
    await homePage.newProject({name: projectName})

    await expect(page.getByText(projectName)).toBeVisible()
})
