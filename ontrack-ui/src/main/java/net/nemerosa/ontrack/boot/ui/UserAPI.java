package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.ui.resource.Resource;

import java.util.Optional;

public interface UserAPI {

    Resource<Optional<Account>> getCurrentUser();

    Form loginForm();

    Resource<Optional<Account>> login();

}
