const {expect} = require('@playwright/test');
const {login} = require("../login");
const {UserProfilePage} = require("./userProfile");
const {generate} = require("@ontrack/utils");
const {test} = require("../../fixtures/connection");

const generateToken = async (page, ontrack) => {
    // Login
    await login(page, ontrack)
    // Going to the user profile page
    const userProfilePage = new UserProfilePage(page, ontrack)
    await userProfilePage.goTo()
    // Generates and copies the token
    const tokenName = generate("tok_")
    const token = await userProfilePage.generateToken(tokenName)
    // OK
    return {
        tokenName,
        token,
    }
}

test('Generate a token with the UI and use it in the API', async ({page, ontrack}) => {
    // Creating a project
    const project = await ontrack.createProject()
    // Token & account
    const {token} = await generateToken(page, ontrack)
    // Getting the list of projects using the API and the token
    const ontrackWithToken = ontrack.withToken(token)
    const projects = await ontrackWithToken.projectList()
    // Checking the project created above is part of this list
    await expect(projects.map(it => it.name)).toContain(project.name)
})
