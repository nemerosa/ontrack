package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SVNChangeLogFile {

    private final String path;
    private final String url;
    private final List<SVNChangeLogFileChange> changes = new ArrayList<>();

    public SVNChangeLogFile addChange(SVNChangeLogFileChange change) {
        changes.add(change);
        return this;
    }

}
