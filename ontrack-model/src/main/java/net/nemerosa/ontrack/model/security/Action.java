package net.nemerosa.ontrack.model.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Action {

    private final String id;
    private final String name;
    private final String uriFragment;

    public static Action of(String id, String name, String uriFragment, Object... parameters) {
        return new Action(id, name, String.format(uriFragment, parameters));
    }

}
