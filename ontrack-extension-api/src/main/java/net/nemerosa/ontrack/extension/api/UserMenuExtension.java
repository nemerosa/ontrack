package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.security.Action;

public interface UserMenuExtension extends GlobalExtension {

    // TODO Allows a list of actions to be defined
    Action getAction();

}
