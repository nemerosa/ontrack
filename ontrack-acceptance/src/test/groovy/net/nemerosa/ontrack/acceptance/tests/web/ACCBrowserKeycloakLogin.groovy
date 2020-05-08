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
        withKeycloakConfigured { realm ->
            browser { browser ->
                def loginPage = goTo(LoginPage, [:])
                assert loginPage.hasExtension(realm): "OIDC extension is present"
                def keycloakLoginPage = loginPage.useExtension(realm)
                def homePage = keycloakLoginPage.login("ontrack-user", "secret")
                def userName = homePage.header.userName
                assert userName == "User"
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

            createUser(
                    users,
                    "ontrack-admin",
                    "Admin",
                    "Ontrack",
                    ["ontrack-admin", "ontrack-user", "other-group"],
                    "ontrack-admin@nemerosa.net"
            )

            createUser(
                    users,
                    "ontrack-user",
                    "User",
                    "Ontrack",
                    ["ontrack-user", "other-group"],
                    "ontrack-user@nemerosa.net"
            )

            // Creates an application
            def clientAdmin = adminClient.realm(realm).clients()
            def clientId = uid("C")
            def client = new ClientRepresentation()
            client.clientId = clientId
            client.baseUrl = baseURL
            client.enabled = true
            client.redirectUris = [
                    "${baseURL}/login/oauth2/code/$realm" as String
            ]
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
                code(realm)
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

    private static void createUser(
            UsersResource users,
            String username,
            String firstName,
            String lastName,
            List<String> groups,
            String email
    ) {
        def credentials = new CredentialRepresentation()
        credentials.type = CredentialRepresentation.PASSWORD
        credentials.temporary = false
        credentials.secretData = "secret"

        def user = new UserRepresentation()
        user.credentials = [credentials]
        user.username = username
        user.firstName = firstName
        user.lastName = lastName
        user.groups = groups
        user.email = email
        user.emailVerified = true
        user.enabled = true
        users.create(user)
    }

}
