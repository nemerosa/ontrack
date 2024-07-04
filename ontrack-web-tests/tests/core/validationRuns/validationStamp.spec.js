// @ts-check
const {test, expect} = require('@playwright/test');
const {ontrack} = require("@ontrack/ontrack");
const {login} = require("../login");
const {generate} = require("@ontrack/utils");
const {ValidationStampPage} = require("./validationStamp");

test('display last status description in validation stamp page', async ({page}) => {
    // Provisioning
    const project = await ontrack().createProject()
    const branch = await project.createBranch()
    const validationStamp = await branch.createValidationStamp()
    const build = await branch.createBuild()
    const description = generate("Description ");
    await build.validate(validationStamp, {description: description})
    // Login
    await login(page)
    // Navigating to the validation stamp
    const vsPage = new ValidationStampPage(page, validationStamp)
    await vsPage.goTo()
    // Expecting the last run description to appear
    await expect(page.getByText(description)).toBeVisible()
})