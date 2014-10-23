package net.nemerosa.ontrack.model.structure;

/**
 * Policy to apply when a branch is configured but no longer available.
 */
public enum TemplateSynchronisationAbsencePolicy implements Describable {

    /**
     * Disable (default)
     */
    DISABLE("Disable", "Disables the branch."),

    /**
     * Deletes the branch
     */
    DELETE("Delete", "Deletes the branch.");

    private final String name;
    private final String description;

    TemplateSynchronisationAbsencePolicy(String name, String description) {
        this.name = name;
        this.description = description;
    }


    @Override
    public String getId() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
