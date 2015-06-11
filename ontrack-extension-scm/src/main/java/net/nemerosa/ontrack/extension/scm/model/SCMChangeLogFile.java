package net.nemerosa.ontrack.extension.scm.model;

import java.util.List;

public interface SCMChangeLogFile {

    /**
     * Gets the reference path
     */
    String getPath();

    /**
     * Change types
     */
    List<SCMChangeLogFileChangeType> getChangeTypes();

}
