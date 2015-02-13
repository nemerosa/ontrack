package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class SVNChangeLogFile implements SCMChangeLogFile {

    private final String path;
    private final String url;
    private final List<SVNChangeLogFileChange> changes = new ArrayList<>();

    public SVNChangeLogFile addChange(SVNChangeLogFileChange change) {
        changes.add(change);
        return this;
    }

}
