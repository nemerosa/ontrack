package net.nemerosa.ontrack.boot.resource;

import net.nemerosa.ontrack.boot.support.UIException;

public class LinkNoSupplierException extends UIException {
    public LinkNoSupplierException(String uri) {
        super("Link [%s] has no supplier.", uri);
    }
}
