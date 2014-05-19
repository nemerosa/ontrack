package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.security.ConnectedAccount;
import net.nemerosa.ontrack.ui.resource.Resource;

public interface UserAPI {

    Resource<ConnectedAccount> getCurrentUser();

    Form loginForm();

    Resource<ConnectedAccount> login();

}
