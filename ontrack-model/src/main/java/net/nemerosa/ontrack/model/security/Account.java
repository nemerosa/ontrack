package net.nemerosa.ontrack.model.security;

public interface Account {

    boolean isGranted(Class<? extends GlobalFunction> globalFunction);

    String getName();

    SecurityRole getRole();
}
