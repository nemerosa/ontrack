package net.nemerosa.ontrack.model.structure;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class SyncResult {

    @Getter
    @Setter
    private int unknownTargetIgnored = 0;
    @Getter
    @Setter
    private int unknownTargetDeleted = 0;
    @Getter
    private int created = 0;
    @Getter
    private int presentTargetIgnored = 0;
    @Getter
    private int presentTargetReplaced = 0;

    public static SyncResult empty() {
        return new SyncResult();
    }

    public void create() {
        created++;
    }

    public void ignorePresentTarget() {
        presentTargetIgnored++;
    }

    public void replacePresentTarget() {
        presentTargetReplaced++;
    }
}
