/**
 * Testing specific authentication modes
 */

const {test} = require("../fixtures/connection");
const {login} = require("./login");
const {UserProfilePage} = require("./user/userProfile");
const {generate} = require("@ontrack/utils");

test('Mapped groups', {tag: "@auth"}, async ({page, ontrack}) => {
    // Setting up the groups & mappings
    const groupName = generate("grp-")
    await ontrack.admin().createGroup({name: groupName, description: ""})
    await ontrack.admin().mapGroup("/ReadOnly", groupName)
    // Login with specific user
    await login(page, ontrack, "demo@ontrack.local", "demo")
    // Navigate to the user profile page
    const userProfilePage = new UserProfilePage(page, ontrack)
    await userProfilePage.goTo()
    // Checking the groups
    await userProfilePage.checkGroups({
        assignedGroups: [],
        mappedGroups: [groupName],
        idpGroups: ["/ReadOnly"]
    })
})
