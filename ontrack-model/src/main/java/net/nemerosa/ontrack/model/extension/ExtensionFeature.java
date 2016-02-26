package net.nemerosa.ontrack.model.extension;

public interface ExtensionFeature {

    String getId();

    String getName();

    String getDescription();

    /**
     * Gets the version of this feature
     */
    String getVersion();

    default ExtensionFeatureOptions getOptions() {
        return ExtensionFeatureOptions.DEFAULT;
    }

    default ExtensionFeatureDescription getFeatureDescription() {
        return new ExtensionFeatureDescription(
                getId(),
                getName(),
                getDescription(),
                getVersion(),
                getOptions()
        );
    }

}
