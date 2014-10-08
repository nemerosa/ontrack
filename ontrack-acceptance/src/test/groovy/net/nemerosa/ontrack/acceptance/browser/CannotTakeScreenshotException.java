package net.nemerosa.ontrack.acceptance.browser;

import net.nemerosa.ontrack.common.BaseException;

import java.io.IOException;

public class CannotTakeScreenshotException extends BaseException {
    public CannotTakeScreenshotException(String name, IOException e) {
        super(
                String.format(
                        "Cannot take %s screenshot",
                        name
                ),
                e
        );
    }
}
