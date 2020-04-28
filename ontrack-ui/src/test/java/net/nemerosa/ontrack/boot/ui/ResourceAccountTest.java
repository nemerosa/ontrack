package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.AuthenticationSource;
import net.nemerosa.ontrack.model.security.ConnectedAccount;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.ui.resource.Resource;
import org.junit.Test;

import java.net.URI;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class ResourceAccountTest {

    @Test
    public void logged_to_json() {
        assertJsonWrite(
                object()
                        .with("_self", "urn:user")
                        .with("authenticationRequired", false)
                        .with("account", object()
                                .with("id", 1)
                                .with("name", "admin")
                                .with("fullName", "Administrator")
                                .with("email", "")
                                .with("authenticationSource", object()
                                        .with("id", "none")
                                        .with("name", "Not defined")
                                        .with("allowingPasswordChange", false)
                                        .with("groupMappingSupported", false)
                                        .end())
                                .with("role", "ADMINISTRATOR")
                                .with("defaultAdmin", true)
                                .end())
                        .with("actions", array().end())
                        .with("logged", true)
                        .end(),
                Resource.of(
                        ConnectedAccount.of(
                                false,
                                Account.of("admin", "Administrator", "", SecurityRole.ADMINISTRATOR, AuthenticationSource.none())
                                        .withId(ID.of(1))
                        ),
                        URI.create("urn:user")
                )
        );
    }

    @Test
    public void not_logged_to_json() {
        assertJsonWrite(
                object()
                        .with("_self", "urn:user")
                        .with("authenticationRequired", true)
                        .with("account", (String) null)
                        .with("actions", array().end())
                        .with("logged", false)
                        .end(),
                Resource.of(
                        ConnectedAccount.none(true),
                        URI.create("urn:user")
                )
        );
    }

}
