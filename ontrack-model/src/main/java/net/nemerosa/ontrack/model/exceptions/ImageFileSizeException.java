package net.nemerosa.ontrack.model.exceptions;

public class ImageFileSizeException extends InputException {
    public ImageFileSizeException(long size, long maxSize) {
        super("Image size (%d K) is too big. It should be at most %d K",
                size / 1024,
                maxSize / 1024);
    }
}
