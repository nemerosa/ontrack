const {login} = require("../../core/login");
const {HomePage} = require("../../core/home/home");
const {generate} = require("@ontrack/utils");
const {test} = require("@playwright/test");

test('creating an environment', async ({page}) => {
    // Login
    await login(page)
    // Going to the environment page, using the button in the home page
    const homePage = new HomePage(page)
    const environmentsPage = await homePage.selectEnvironments()
    // Creating a new environment
    const name = generate("env-")
    await environmentsPage.createEnvironment({
        name: name,
        description: `Description for ${name}`,
        order: 100,
        tags: ['test'],
    })
    // Checks that the environment card is visible
    await environmentsPage.checkEnvironmentIsVisible(name)
})
