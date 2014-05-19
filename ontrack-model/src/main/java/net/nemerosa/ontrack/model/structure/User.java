package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public boolean isAnonymous() {
        return ANONYMOUS.equals(name);
    }

}
