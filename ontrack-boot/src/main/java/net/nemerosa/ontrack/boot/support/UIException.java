package net.nemerosa.ontrack.boot.support;

public abstract class UIException extends RuntimeException {

    public UIException(String pattern, Object... params) {
        super(String.format(pattern, params));
    }
}
