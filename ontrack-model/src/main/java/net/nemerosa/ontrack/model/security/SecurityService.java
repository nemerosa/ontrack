package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.annotations.GlobalFunction;

public interface SecurityService {

    void checkGlobalFunction(Class<? extends GlobalFunction> fn);

    boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn);

}
