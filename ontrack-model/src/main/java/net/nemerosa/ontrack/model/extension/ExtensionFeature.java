package net.nemerosa.ontrack.model.extension;

public interface ExtensionFeature {

    String getId();

    String getName();

    String getDescription();

    default ExtensionFeatureOptions getOptions() {
        return ExtensionFeatureOptions.DEFAULT;
    }

    default ExtensionFeatureDescription getFeatureDescription() {
        return new ExtensionFeatureDescription(
                getId(),
                getName(),
                getDescription(),
                getOptions()
        );
    }

}
