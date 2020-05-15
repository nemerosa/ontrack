package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import net.nemerosa.ontrack.acceptance.browser.pages.LoginPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.*

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCBrowserKeycloakLogin extends AcceptanceTestClient {

    @Test
    void 'Login with Keycloak'() {
        withKeycloakConfigured { realm, _, userSimple ->
            browser { browser ->
                def loginPage = goTo(LoginPage, [:])
                assert loginPage.hasExtension(realm): "OIDC extension is present"
                def keycloakLoginPage = loginPage.useExtension(realm)
                def homePage = keycloakLoginPage.login(userSimple, "secret")
                def userName = homePage.header.userName
                assert userName == "User ${userSimple}"
            }
        }
    }

    @Test
    void 'Login with Keycloak and sets as admin'() {
        withKeycloakConfigured { String realm, userAdmin, _ ->
            browser { browser ->
                // Initial login
                def loginPage = goTo(LoginPage, [:])
                assert loginPage.hasExtension(realm): "OIDC extension is present"
                def keycloakLoginPage = loginPage.useExtension(realm)
                def homePage = keycloakLoginPage.login(userAdmin, "secret")
                def userName = homePage.header.userName
                assert userName == "Admin ${userAdmin}"
                loginPage = homePage.logout()
                // Setup of group mappings
                ontrack.admin.setGroupMapping("oidc", realm, "ontrack-admin", "Administrators")
                ontrack.admin.setGroupMapping("oidc", realm, "ontrack-user", "Read-Only")
                // Re-login
                assert loginPage.hasExtension(realm): "OIDC extension is present"
                homePage = loginPage.useExtension(realm, HomePage) // We are already authenticated in Keycloak, going directly to the Home page
                // Checks the user can create a project
                def projectName = uid('P')
                homePage.createProject {
                    name = projectName
                    description = "Project ${projectName}"
                }
                // Checks the project is visible in the list
                assert homePage.isProjectPresent(projectName)
            }
        }
    }

    private void withKeycloakConfigured(Closure code) {

        Keycloak adminClient = KeycloakBuilder.builder()
                .serverUrl("${configRule.config.keycloakUri}/auth")
                .realm("master")
                .clientId("admin-cli")
                .clientSecret(null)
                .username(configRule.config.keycloakUsername)
                .password(configRule.config.keycloakPassword)
                .build()

        def realm = uid("r")
        def representation = new RealmRepresentation()
        representation.realm = realm
        representation.enabled = true
        adminClient.realms().create(representation)

        try {

            // Creates three roles
            def roles = adminClient.realm(realm).roles()
            ["ontrack-admin", "ontrack-user", "other-role"].each { role ->
                def rep = new RoleRepresentation()
                rep.setName(role)
                roles.create(rep)
            }

            // Creates two users
            def users = adminClient.realm(realm).users()

            def userAdmin = createUser(
                    users,
                    "Admin",
                    ["ontrack-admin", "ontrack-user", "other-role"]
            )

            def userSimple = createUser(
                    users,
                    "User",
                    ["ontrack-user", "other-role"]
            )

            // Creates an application
            def clientAdmin = adminClient.realm(realm).clients()
            def clientId = uid("C")
            def client = new ClientRepresentation()
            client.protocol = "openid-connect"
            client.clientId = clientId
            client.baseUrl = baseURL
            client.enabled = true
            client.redirectUris = [
                    "${baseURL}/*" as String
            ]
            client.baseUrl = "${baseURL}/login/oauth2/code/$realm" as String
            client.webOrigins = [baseURL]
            client.directAccessGrantsEnabled = true
            clientAdmin.create(client)

            ontrack.config.oidcSettings.createProvider(
                    realm,
                    "Test $realm",
                    "",
                    "${configRule.config.keycloakUri}/auth/realms/$realm",
                    clientId,
                    "",
                    ".*"
            )
            try {
                code(realm, userAdmin, userSimple)
            } finally {
                if (configRule.config.keycloakCleanup) {
                    ontrack.config.oidcSettings.deleteProvider(realm)
                }
            }

        } finally {
            if (configRule.config.keycloakCleanup) {
                adminClient.realm(realm).remove()
            }
        }
    }

    private static String createUser(
            UsersResource users,
            String firstName,
            List<String> roles
    ) {

        def username = uid("U")

        def user = new UserRepresentation()
        user.requiredActions = []
        user.username = username
        user.firstName = firstName
        user.lastName = username
        user.realmRoles = roles
        user.email = "${username}@nemerosa.net"
        user.emailVerified = true
        user.enabled = true
        users.create(user)

        def userId = users.search(username).first().id
        def userClient = users.get(userId)

        def credentials = new CredentialRepresentation()
        credentials.type = CredentialRepresentation.PASSWORD
        credentials.temporary = false
        credentials.value = "secret"
        userClient.resetPassword(credentials)

        return username
    }

}
