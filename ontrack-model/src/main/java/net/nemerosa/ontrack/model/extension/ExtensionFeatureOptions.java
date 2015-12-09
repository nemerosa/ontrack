package net.nemerosa.ontrack.model.extension;

import lombok.Data;
import lombok.experimental.Builder;
import lombok.experimental.Wither;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
            .dependencies(Collections.<ExtensionFeatureDescription>emptySet())
            .build();

    /**
     * Does the extension provides some web components?
     */
    @Wither
    private final boolean gui;

    /**
     * List of extensions this feature depends on.
     */
    @Wither
    private final Set<ExtensionFeatureDescription> dependencies;

    /**
     * Adds a dependency
     */
    public ExtensionFeatureOptions withDependency(ExtensionFeature feature) {
        Set<ExtensionFeatureDescription> existing = this.dependencies;
        Set<ExtensionFeatureDescription> newDependencies;
        if (existing == null) {
            newDependencies = new HashSet<>();
        } else {
            newDependencies = new HashSet<>(existing);
        }
        newDependencies.add(feature.getFeatureDescription());
        return withDependencies(newDependencies);
    }

}
