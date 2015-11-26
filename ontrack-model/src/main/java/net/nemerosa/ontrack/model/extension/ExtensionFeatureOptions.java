package net.nemerosa.ontrack.model.extension;

import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

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
    @Wither
    private final boolean gui;

}
