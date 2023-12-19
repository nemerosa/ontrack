// @ts-check
const {test, expect} = require('@playwright/test');
const {ontrack} = require("@ontrack/ontrack");
const {login, logout} = require("../login");
const {UserProfilePage} = require("./userProfile");
const {generate} = require("@ontrack/utils");

const generateToken = async (page) => {
    // Creating a new account
    const {username, password} = await ontrack().admin().createAccount()
    // Login using this account
    await login(page, username, password)
    // Going to the user profile page
    const userProfilePage = new UserProfilePage(page)
    await userProfilePage.goTo()
    // Generates and copies the token
    const tokenName = generate("tok_")
    const token = await userProfilePage.generateToken(tokenName)
    // OK
    return {
        username,
        password,
        tokenName,
        token,
    }
}

test('Generate a token with the UI and use it as password in the UI', async ({page}) => {
    // Token & account
    const {username, token} = await generateToken(page)
    // Logout
    await logout(page)
    // Login with the token
    await login(page, username, token)
})

test('Generate a token with the UI and use it in the API', async ({page}) => {
    // Creating a project
    const project = await ontrack().createProject()
    // Token & account
    const {username, token} = await generateToken(page)
    // Getting the list of projects using the API and the token
    const projects = await ontrack({
        username,
        password: token,
    }).projectList()
    // Checking the project created above is part of this list
    await expect(projects.map(it => it.name)).toContain(project.name)
})

test('Trying the connect with an invalid token', async ({page}) => {
    // Token & account
    const {username, password, tokenName, token} = await generateToken(page)
    // Logs out
    await logout(page)
    // Revokes the generated token using the API
    await ontrack({username, password}).admin().revokeToken(tokenName)
    // Tries to login; it must fail
    await login(page, username, token, {message: "Invalid username and password."})
})
