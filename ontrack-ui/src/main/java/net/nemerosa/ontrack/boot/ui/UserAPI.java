package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.ui.resource.Resource;

import java.util.Optional;

public interface UserAPI {

    Resource<Optional<Account>> getCurrentUser();

    Resource<Optional<Account>> login();

}
