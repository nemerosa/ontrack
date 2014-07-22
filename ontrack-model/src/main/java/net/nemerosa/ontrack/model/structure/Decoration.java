package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * Decoration for an entity.
 * <p>
 * Only the {@link #id id} and the {@link #title} are required. They will be used to access an icon
 * and to display a tooltip. If a {@link #name name} is specified, the representation of the decoration
 * will also include this name directly.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Decoration {

    @JsonIgnore
    private final Class<? extends Decorator> decorator;
    private final String id;
    private final String title;
    private final URI uri;
    private final String name;

    public static Decoration of(Decorator decorator, String id, String title) {
        return new Decoration(decorator.getClass(), id, title, null, null);
    }

    public static Decoration of(Class<? extends Decorator> decorator, String id, String title) {
        return new Decoration(decorator, id, title, null, null);
    }

    public Decoration withUri(URI value) {
        return new Decoration(decorator, id, title, value, name);
    }

    public Decoration withName(String value) {
        return new Decoration(decorator, id, title, uri, value);
    }

    public String getDecorationType() {
        return decorator.getName();
    }

}
