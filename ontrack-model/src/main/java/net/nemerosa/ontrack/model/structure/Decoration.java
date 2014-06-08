package net.nemerosa.ontrack.model.structure;

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

    private final String id;
    private final String title;
    private final URI uri;

    public static Decoration of(String id, String title) {
        return new Decoration(id, title, null);
    }

    public Decoration withUri(URI value) {
        return new Decoration(id, title, value);
    }

}
