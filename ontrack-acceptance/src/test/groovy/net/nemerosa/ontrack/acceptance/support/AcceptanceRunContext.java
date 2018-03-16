package net.nemerosa.ontrack.acceptance.support;

import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the test context
 */
@Data
public class AcceptanceRunContext {

    private final String testDescription;

    public static final AtomicReference<AcceptanceRunContext> instance =
            new AtomicReference<>();

}
