package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class User {

    private final String name;

    public static User of(String name) {
        return new User(name);
    }

}
