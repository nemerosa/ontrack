package net.nemerosa.ontrack.model.security;

public interface Account {

    String getName();

    SecurityRole getRole();

    boolean isGranted(Class<? extends GlobalFunction> globalFunction);

    boolean isGranted(int projectId, Class<? extends ProjectFunction> fn);
    
}
