package net.nemerosa.ontrack.model.exceptions;

import org.apache.commons.lang3.StringUtils;

public class ImageTypeNotAcceptedException extends InputException {
    public ImageTypeNotAcceptedException(String type, String... imageTypes) {
        super("The image type '%s' is not accepted. It should be one of: %s", type, StringUtils.join(imageTypes, ","));
    }
}
