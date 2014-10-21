package net.nemerosa.ontrack.model.structure;

public enum BranchTemplateSyncType {

    /**
     * The branch was created
     */
    CREATED,

    /**
     * The branch was updated
     */
    UPDATED,

    /**
     * The branch was deleted
     */
    DELETED,

    /**
     * The branch was not taken into account.
     */
    IGNORED,

    /**
     * The branch cannot be instanciated because it is a normal branch.
     */
    EXISTING_CLASSIC,

    /**
     * The branch cannot be instanciated because it is a definition.
     */
    EXISTING_DEFINITION,

    /**
     * The branch cannot be instanciated because it is an instance from another definition.
     */
    EXISTING_INSTANCE_FROM_OTHER,

    /**
     * The branch was disabled
     */
    DISABLED

}
