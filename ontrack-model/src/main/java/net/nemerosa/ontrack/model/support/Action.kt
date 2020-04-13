package net.nemerosa.ontrack.model.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Action {

    private final String id;
    private final String name;
    private final ActionType type;
    private final String uri;

    public static Action of(String id, String name, String uri, Object... parameters) {
        return new Action(id, name, ActionType.LINK, String.format(uri, parameters));
    }

    public static Action form(String id, String name, URI formUri) {
        return new Action(id, name, ActionType.FORM, formUri.toString());
    }

    public Action withUri(String uri) {
        return Action.of(id, name, uri);
    }

}
