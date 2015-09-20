package net.nemerosa.ontrack.model.extension;

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
