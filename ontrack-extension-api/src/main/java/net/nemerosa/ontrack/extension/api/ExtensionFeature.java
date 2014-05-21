package net.nemerosa.ontrack.extension.api;

public interface ExtensionFeature {

    String getId();

    String getName();

    String getDescription();

    default ExtensionFeatureDescription getFeatureDescription() {
        return new ExtensionFeatureDescription(
                getId(),
                getName(),
                getDescription()
        );
    }

}
