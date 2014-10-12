package net.nemerosa.ontrack.model.structure;

/**
 * For an object which can have an ID, a name and a description.
 */
public interface Describable {

    String getId();

    String getName();

    String getDescription();

    default Description toDescription() {
        return new Description(
                getId(),
                getName(),
                getDescription()
        );
    }

}
