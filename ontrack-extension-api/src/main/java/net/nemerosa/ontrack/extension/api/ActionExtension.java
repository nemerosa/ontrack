package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.security.Action;

/**
 * Extension that can return an action.
 */
public interface ActionExtension extends Extension {

    Action getAction();

}
