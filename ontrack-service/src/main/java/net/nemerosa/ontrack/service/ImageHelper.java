package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException;
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException;
import org.apache.commons.lang3.ArrayUtils;

public final class ImageHelper {

    private static final long ICON_IMAGE_SIZE_MAX = 16 * 1000L;

    private static final String[] ACCEPTED_IMAGE_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif"
    };

    private ImageHelper() {
    }

    public static void checkImage(Document document) {
        // Checks the image type
        if (document != null && !ArrayUtils.contains(ACCEPTED_IMAGE_TYPES, document.getType())) {
            throw new ImageTypeNotAcceptedException(document.getType(), ACCEPTED_IMAGE_TYPES);
        }
        // Checks the image length
        int size = document != null ? document.getContent().length : 0;
        if (size > ICON_IMAGE_SIZE_MAX) {
            throw new ImageFileSizeException(size, ICON_IMAGE_SIZE_MAX);
        }
    }
}
