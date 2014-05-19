package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.structure.Signature;

public interface SecurityService {

    void checkGlobalFunction(Class<? extends GlobalFunction> fn);

    boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn);

    void checkProjectFunction(int projectId, Class<? extends ProjectFunction> fn);

    boolean isProjectFunctionGranted(int projectId, Class<? extends ProjectFunction> fn);

    Account getCurrentAccount();

    Signature getCurrentSignature();
}
