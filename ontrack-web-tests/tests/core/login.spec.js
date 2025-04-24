// @ts-check
const {expect} = require('@playwright/test');
const {login, logout} = require("./login");
const {test} = require("../fixtures/connection");

test('login', async ({page, ontrack}) => {
    await login(page, ontrack)
})

test('login and logout', async ({page, ontrack}) => {
    await login(page, ontrack)
    await logout(page)
})
