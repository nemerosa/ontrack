package net.nemerosa.ontrack.boot.resource;

import net.nemerosa.ontrack.boot.support.UIException;

public class LinkNotFoundException extends UIException {
    public LinkNotFoundException(String link) {
        super("Cannot find link %s", link);
    }
}
