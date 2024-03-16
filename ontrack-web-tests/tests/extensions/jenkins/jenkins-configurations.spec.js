// @ts-check
const {test, expect} = require('@playwright/test')
const {login} = require("../../core/login");
const {JenkinsConfigurationsPage} = require("./jenkins-configurations");
const {generate} = require("@ontrack/utils");

test('creation of a Jenkins configuration', async ({page}) => {
    // Login
    await login(page)
    // Going to the Jenkins configurations page
    const jenkinsConfigurationsPage = new JenkinsConfigurationsPage(page)
    await jenkinsConfigurationsPage.goTo()
    // Creating a new configuration
    const url = `https://${generate("jen-")}.com`
    await jenkinsConfigurationsPage.createConfig({
        name: generate("jen-"),
        url: url,
        user: "some-user",
        password: "some-password",
    })
    // Checks the configuration has been created
    await jenkinsConfigurationsPage.checkConfigurationCreated(url)
})

test('testing a Jenkins configuration', async ({page}) => {
    // Login
    await login(page)
    // Going to the Jenkins configurations page
    const jenkinsConfigurationsPage = new JenkinsConfigurationsPage(page)
    await jenkinsConfigurationsPage.goTo()
    // Creating a new configuration
    const name = generate("jen-");
    const url = `https://${name}.com`
    await jenkinsConfigurationsPage.createConfig({
        name: name,
        url: url,
        user: "some-user",
        password: "some-password",
    })
    // Testing this configuration
    await jenkinsConfigurationsPage.testConfiguration(name)
})