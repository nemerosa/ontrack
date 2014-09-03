package net.nemerosa.ontrack.extension.issues.model;

import net.nemerosa.ontrack.common.BaseException;

import java.util.Collection;

public class IssueExportMoreThanOneGroupException extends BaseException {

    public IssueExportMoreThanOneGroupException(String key, Collection<String> groups) {
        super("Issue %s has been assigned to more than one group: %s", key, groups);
    }
}
