package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

@Data
public class SVNChangeLogReference {

    private final String path;
    private final long start;
    private final long end;

    public boolean isNone() {
        return start == end;
    }
    
}
