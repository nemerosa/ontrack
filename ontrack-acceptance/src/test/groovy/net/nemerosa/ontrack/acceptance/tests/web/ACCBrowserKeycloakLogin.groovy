package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
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
        withKeycloakConfigured { realm, userAdmin, userSimple ->
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

            // Creates three groups
            def groups = adminClient.realm(realm).groups()
            ["ontrack-admin", "ontrack-user", "other-group"].each { group ->
                def rep = new GroupRepresentation()
                rep.setName(group)
                groups.add(rep)
            }

            // Creates two users
            def users = adminClient.realm(realm).users()

            def userAdmin = createUser(
                    users,
                    "Admin",
                    ["ontrack-admin", "ontrack-user", "other-group"]
            )

            def userSimple = createUser(
                    users,
                    "User",
                    ["ontrack-user", "other-group"]
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
            List<String> groups
    ) {

        def username = uid("U")

        def user = new UserRepresentation()
        user.requiredActions = []
        user.username = username
        user.firstName = firstName
        user.lastName = username
        user.groups = groups
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
