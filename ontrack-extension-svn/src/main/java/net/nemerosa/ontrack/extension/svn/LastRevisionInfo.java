package net.nemerosa.ontrack.extension.svn;

import lombok.Data;

@Data
public class LastRevisionInfo {

    private final long revision;
    private final String message;
    private final long repositoryRevision;

    public static LastRevisionInfo none() {
        return new LastRevisionInfo(0L, "", 0L);
    }

    public boolean isNone() {
        return revision == 0L;
    }
}
