package net.nemerosa.ontrack.extension.issues.model;

import net.nemerosa.ontrack.common.BaseException;

import java.util.Collection;

/**
 * @deprecated Will be removed in V5. Use the SCM change log service
 */
@Deprecated
public class IssueExportMoreThanOneGroupException extends BaseException {

    public IssueExportMoreThanOneGroupException(String key, Collection<String> groups) {
        super("Issue %s has been assigned to more than one group: %s", key, groups);
    }
}
