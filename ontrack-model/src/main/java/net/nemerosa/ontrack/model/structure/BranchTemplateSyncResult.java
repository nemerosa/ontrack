package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.events.Event;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BranchTemplateSyncResult {

    private final String branchName;
    private final BranchTemplateSyncType type;
    private String sourceName;
    @Wither(AccessLevel.PRIVATE)
    private String otherTemplateName;

    protected static BranchTemplateSyncResult of(String name, BranchTemplateSyncType type, String sourceName) {
        return new BranchTemplateSyncResult(name, type, sourceName, "");
    }

    public static BranchTemplateSyncResult ignored(String name) {
        return BranchTemplateSyncResult.of(name, BranchTemplateSyncType.IGNORED, "");
    }

    public static BranchTemplateSyncResult deleted(String name) {
        return of(name, BranchTemplateSyncType.DELETED, "");
    }

    public static BranchTemplateSyncResult disabled(String name) {
        return of(name, BranchTemplateSyncType.DISABLED, "");
    }

    public static BranchTemplateSyncResult existingClassic(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.EXISTING_CLASSIC, sourceName);
    }

    public static BranchTemplateSyncResult existingDefinition(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.EXISTING_DEFINITION, sourceName);
    }

    public static BranchTemplateSyncResult existingInstanceFromOther(String name, String sourceName, String otherTemplateName) {
        return of(name, BranchTemplateSyncType.EXISTING_INSTANCE_FROM_OTHER, sourceName).withOtherTemplateName(otherTemplateName);
    }

    public static BranchTemplateSyncResult updated(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.UPDATED, sourceName);
    }

    public static BranchTemplateSyncResult created(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.CREATED, sourceName);
    }

    public Event event(StructureService structureService, Branch templateBranch) {
        return type.event(structureService, templateBranch, this);
    }
}
