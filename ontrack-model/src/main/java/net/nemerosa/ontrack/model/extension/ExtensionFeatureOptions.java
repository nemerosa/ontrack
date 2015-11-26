package net.nemerosa.ontrack.model.extension;

import lombok.Data;
import lombok.experimental.Builder;

/**
 * Options for a feature
 */
@Data
@Builder
public class ExtensionFeatureOptions {

    /**
     * Default options
     */
    public static final ExtensionFeatureOptions DEFAULT = builder()
            .gui(false)
            .build();

    /**
     * Does the extension provides some web components?
     */
    private final boolean gui;

}
