package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Connecting with tokens & managing the tokens.
 */
class ACCDSLTokens extends AbstractACCDSL {

    @Test
    void 'Connecting with a token'() {
        // Creating a project
        def name = uid("P")
        ontrack.project(name, "Test project")
        // Generates a token for current user
        def token = ontrackAsAnyUser.tokens.generate()
        // Creates a connection using this token
        def ontrackWithToken = ontrackBuilder.authenticate(token.value).build()
        // Checks we can still connect, and get the list of projects
        def project = ontrackWithToken.projects.find { it.name == name }
        assert project != null: "Project still accessible through token authentication"
    }

    @Test
    void 'Connecting with a token as a password'() {
        // Creating a project
        def name = uid("P")
        ontrack.project(name, "Test project")
        // Generates a token for current user
        def user = uid("U")
        def token = getOntrackAsAnyUser(user).tokens.generate()
        // Creates a connection using this token as a password
        def ontrackWithToken = ontrackBuilder.authenticate(user, token.value).build()
        // Checks we can still connect, and get the list of projects
        def project = ontrackWithToken.projects.find { it.name == name }
        assert project != null: "Project still accessible through token authentication"
    }

    @Test
    void 'Connecting with a token is not possible after it has been revoked'() {
        // Creating a project
        def name = uid("P")
        ontrack.project(name, "Test project")
        // Generates a token for current user
        def token = ontrackAsAnyUser.tokens.generate()
        // Creates a connection using this token
        def ontrackWithToken = ontrackBuilder.authenticate(token.value).build()
        // Checks we can still connect, and get the list of projects
        def project = ontrackWithToken.projects.find { it.name == name }
        assert project != null: "Project still accessible through token authentication"
        // Revokes all tokens
        ontrack.tokens.revokeAll()
        // We cannot connect any longer
        try {
            ontrackWithToken.projects
            assert false: "Authentication should have been refused"
        } catch (Exception ignored) {
            assert true: "Authentication is rejected"
        }
    }

    @Test
    void 'Generating a token'() {
        def ontrack = ontrackAsAnyUser
        // Generates a token for current user
        def token = ontrack.tokens.generate()
        assert token.value != null && token.value != ""
        assert token.creation != null
        assert token.validUntil == null
    }

    @Test
    void 'Getting the current token returns null when not generated yet'() {
        def ontrack = ontrackAsAnyUser
        // Gets the current token
        def current = ontrack.tokens.current
        // Null
        assert current == null
    }

    @Test
    void 'Getting the current token'() {
        def ontrack = ontrackAsAnyUser
        // Generates a token for current user
        def token = ontrack.tokens.generate()
        // Gets the current token
        def current = ontrack.tokens.current
        // Equality
        assert current == token
    }

}
