package net.nemerosa.ontrack.model.buildfilter;

import net.nemerosa.ontrack.model.exceptions.NotFoundException;

public class BuildFilterProviderDataParsingException extends NotFoundException {

    public BuildFilterProviderDataParsingException(String type) {
        super("Could not parse filter data for type: %s", type);
    }
}
