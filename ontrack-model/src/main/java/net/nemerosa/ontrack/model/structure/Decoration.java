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

    private final String title;
    private final String cls;
    private final String iconPath;
    private final URI uri;

    public static Decoration of(String title) {
        return new Decoration(title, "", "", null);
    }

    public Decoration withCls(String value) {
        return new Decoration(title, value, iconPath, uri);
    }

    public Decoration withIconPath(String value) {
        return new Decoration(title, cls, value, uri);
    }

    public Decoration withUri(URI value) {
        return new Decoration(title, cls, iconPath, value);
    }

}
