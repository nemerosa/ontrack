// @ts-check
const {login, logout} = require("./login");
const {test} = require("../fixtures/connection");

test('login', {tag: "@auth"}, async ({page, ontrack}) => {
    await login(page, ontrack)
})

test('login and logout', {tag: "@auth"}, async ({page, ontrack}) => {
    await login(page, ontrack)
    await logout(page)
})
