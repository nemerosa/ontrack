package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.browser.pages.LoginPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.idm.UserRepresentation

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCBrowserKeycloakLogin extends AcceptanceTestClient {

    @Test
    void 'Login with Keycloak'() {
        withKeycloakConfigured { realm ->
            browser { browser ->
                def loginPage = goTo(LoginPage, [:])
                assert loginPage.hasExtension(realm) : "OIDC extension is present"
                loginPage.useExtension(realm)
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
        representation.setRealm(realm)
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

            def userAdmin = new UserRepresentation()
            userAdmin.username = "ontrack-admin"
            userAdmin.firstName = "Admin"
            userAdmin.lastName = "Ontrack"
            userAdmin.groups = ["ontrack-admin", "ontrack-user", "other-group"]
            userAdmin.email = "ontrack-admin@nemerosa.net"
            userAdmin.emailVerified = true
            users.create(userAdmin)

            def user = new UserRepresentation()
            user.username = "ontrack-user"
            user.firstName = "User"
            user.lastName = "Ontrack"
            user.groups = ["ontrack-user", "other-group"]
            user.email = "ontrack-user@nemerosa.net"
            user.emailVerified = true
            users.create(user)

            // Creates an application
            def clientAdmin = adminClient.realm(realm).clients()
            def clientId = uid("C")
            def client = new ClientRepresentation()
            client.clientId = clientId
            client.baseUrl = baseURL
            client.rootUrl = baseURL
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
                ontrack.config.oidcSettings.deleteProvider(realm)
            }

        } finally {
            adminClient.realm(realm).remove()
        }
    }

}
