package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.SimpleEventType;

public enum BranchTemplateSyncType {

    /**
     * The branch was created
     */
    CREATED {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_created",
                            "Branch ${BRANCH} has been created from template ${:template} using source ${:source}."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .with("source", result.getSourceName())
                    .get();
        }
    },

    /**
     * The branch was updated
     */
    UPDATED {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_updated",
                            "Branch ${BRANCH} has been updated from template ${:template} using source ${:source}."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .with("source", result.getSourceName())
                    .get();
        }
    },

    /**
     * The branch was deleted
     */
    DELETED {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_deleted",
                            "Branch ${:branch} has been deleted because it is no longer in sources of template ${:template}."
                    )
            )
                    .with("branch", result.getBranchName())
                    .with("template", templateBranch.getName())
                    .get();
        }
    },

    /**
     * The branch was not taken into account.
     */
    IGNORED {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_ignored",
                            "Branch ${BRANCH} has been ignored because it has already been disabled. You should " +
                                    "delete this branch or exclude it from the sources of the ${:template} template."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .get();
        }
    },

    /**
     * The branch cannot be instanciated because it is a normal branch.
     */
    EXISTING_CLASSIC {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_existing_classic",
                            "Branch ${BRANCH} cannot be synchronised with template ${:template} using source {:source} " +
                                    "since it already exists. Either delete this branch or exclude it from the sources."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .with("source", result.getSourceName())
                    .get();
        }
    },

    /**
     * The branch cannot be instanciated because it is a definition.
     */
    EXISTING_DEFINITION {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_existing_definition",
                            "Branch ${BRANCH} cannot be synchronised with template ${:template} using source {:source} " +
                                    "since it is a template definition itself. Either delete this branch or exclude " +
                                    "it from the sources."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .with("source", result.getSourceName())
                    .get();
        }
    },

    /**
     * The branch cannot be instanciated because it is an instance from another definition.
     */
    EXISTING_INSTANCE_FROM_OTHER {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_existing_instance_from_other",
                            "Branch ${BRANCH} cannot be synchronised with template ${:template} using source {:source} " +
                                    "since it already an instance from the ${:otherTemplate} template. Either delete " +
                                    "this branch or exclude it from the sources."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .with("otherTemplate", result.getOtherTemplateName())
                    .with("source", result.getSourceName())
                    .get();
        }
    },

    /**
     * The branch was disabled
     */
    DISABLED {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(
                    SimpleEventType.of(
                            "sync_disabled",
                            "Branch ${BRANCH} has been disabled because it is no longer in sources of template ${:template}. " +
                                    "It should either be deleted or excluded from the sources of the template."
                    )
            )
                    .withBranch(getBranch(structureService, result, templateBranch))
                    .with("template", templateBranch.getName())
                    .with("otherTemplate", result.getOtherTemplateName())
                    .with("source", result.getSourceName())
                    .get();
        }
    };

    private static Branch getBranch(StructureService structureService, BranchTemplateSyncResult result, Branch templateBranch) {
        return structureService.findBranchByName(templateBranch.getProject().getName(), result.getBranchName()).get();
    }

    public abstract Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result);

}
