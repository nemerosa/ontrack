// @ts-check
const {test, expect} = require('@playwright/test');
const {login, logout} = require("./login");

test('login', async ({page}) => {
    await login(page)
})

test('login and logout', async ({page}) => {
    await login(page)
    await logout(page)
})
