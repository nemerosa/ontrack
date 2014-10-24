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
    private final String description;
    private String sourceName;
    @Wither(AccessLevel.PRIVATE)
    private String otherTemplateName;

    protected static BranchTemplateSyncResult of(String name, BranchTemplateSyncType type, String sourceName, String description) {
        return new BranchTemplateSyncResult(name, type, description, sourceName, "");
    }

    public static BranchTemplateSyncResult ignored(String name) {
        return BranchTemplateSyncResult.of(name, BranchTemplateSyncType.IGNORED, "", "Branch has not been taken into account.");
    }

    public static BranchTemplateSyncResult deleted(String name) {
        return of(name, BranchTemplateSyncType.DELETED, "", "Branch has been deleted.");
    }

    public static BranchTemplateSyncResult disabled(String name) {
        return of(name, BranchTemplateSyncType.DISABLED, "", "Branch has been disabled.");
    }

    public static BranchTemplateSyncResult existingClassic(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.EXISTING_CLASSIC, sourceName, "Branch already exists.");
    }

    public static BranchTemplateSyncResult existingDefinition(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.EXISTING_DEFINITION, sourceName, "Branch is a template definition.");
    }

    public static BranchTemplateSyncResult existingInstanceFromOther(String name, String sourceName, String otherTemplateName) {
        return of(name, BranchTemplateSyncType.EXISTING_INSTANCE_FROM_OTHER, sourceName, "Branch is an instance for another template.").withOtherTemplateName(otherTemplateName);
    }

    public static BranchTemplateSyncResult updated(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.UPDATED, sourceName, "Branch has been updated.");
    }

    public static BranchTemplateSyncResult created(String name, String sourceName) {
        return of(name, BranchTemplateSyncType.CREATED, sourceName, "Branch has been created.");
    }

    public Event event(StructureService structureService, Branch templateBranch) {
        return type.event(structureService, templateBranch, this);
    }
}
