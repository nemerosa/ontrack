package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.annotations.GlobalFunction;

public interface SecuredUser {

    boolean isGranted(Class<? extends GlobalFunction> globalFunction);

}
