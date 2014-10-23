package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;

public enum BranchTemplateSyncType {

    /**
     * The branch was created
     */
    CREATED {
        @Override
        public Event event(StructureService structureService, Branch templateBranch, BranchTemplateSyncResult result) {
            return Event.of(EventFactory.SYNC_CREATED)
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
            return Event.of(EventFactory.SYNC_UPDATED)
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
            return Event.of(EventFactory.SYNC_DELETED)
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
            return Event.of(EventFactory.SYNC_IGNORED)
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
            return Event.of(EventFactory.SYNC_EXISTING_CLASSIC)
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
            return Event.of(EventFactory.SYNC_EXISTING_DEFINITION)
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
            return Event.of(EventFactory.SYNC_EXISTING_INSTANCE_FROM_OTHER)
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
            return Event.of(EventFactory.SYNC_DISABLED)
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
