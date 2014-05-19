package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class User {

    public static final String ANONYMOUS = "anonymous";

    private final String name;

    public static User anonymous() {
        return of(ANONYMOUS);
    }

    public static User of(String name) {
        return new User(name);
    }

    public boolean isAnonymous() {
        return ANONYMOUS.equals(name);
    }

}
