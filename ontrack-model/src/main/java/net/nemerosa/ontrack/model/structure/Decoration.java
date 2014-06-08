package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

/**
 * Decoration for an entity.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Decoration {

    @JsonIgnore
    private final Class<? extends Decorator> decorator;
    private final String id;
    private final String title;
    private final URI uri;

    public static Decoration of(Decorator decorator, String id, String title) {
        return new Decoration(decorator.getClass(), id, title, null);
    }

    public static Decoration of(Class<? extends Decorator> decorator, String id, String title) {
        return new Decoration(decorator, id, title, null);
    }

    public Decoration withUri(URI value) {
        return new Decoration(decorator, id, title, value);
    }

    public String getDecorationType() {
        return decorator.getName();
    }

}
