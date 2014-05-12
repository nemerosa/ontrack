package net.nemerosa.ontrack.ui.resource;

import lombok.Data;

import java.net.URI;

/**
 * Definition of a link. A link has a URI and a name.
 */
@Data
public class Link {

    private final String name;
    private final URI uri;

    public static Link of(String name, URI uri) {
        return new Link(name, uri);
    }
}
