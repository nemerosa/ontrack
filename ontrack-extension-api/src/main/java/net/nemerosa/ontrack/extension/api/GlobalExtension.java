package net.nemerosa.ontrack.extension.api;

import net.nemerosa.ontrack.model.security.GlobalFunction;

public interface GlobalExtension extends Extension {

    Class<? extends GlobalFunction> getGlobalFunction();

}
