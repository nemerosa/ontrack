// @ts-check
const {test, expect} = require('@playwright/test');
const {login} = require("./login");

test('login', async ({page}) => {
    await login(page)
})
