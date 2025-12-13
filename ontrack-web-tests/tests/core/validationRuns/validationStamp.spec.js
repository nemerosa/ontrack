const {expect} = require('@playwright/test');
const {login} = require("../login");
const {generate} = require("@ontrack/utils");
const {ValidationStampPage} = require("./validationStamp");
const {test} = require("../../fixtures/connection");

test('display last status description in validation stamp page', async ({page, ontrack}) => {
    // Provisioning
    const project = await ontrack.createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp()
    const build = await branch.createBuild()
    const description = generate("Description ");
    await build.validate(validationStamp, {description: description})
    // Login
    await login(page, ontrack)
    // Navigating to the validation stamp
    const vsPage = new ValidationStampPage(page, validationStamp)
    await vsPage.goTo()
    // Expecting the last run description to appear
    await expect(page.getByText(description)).toBeVisible()
})