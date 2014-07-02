package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLogRevision;

import java.time.LocalDateTime;

public final class SVNServiceUtils {

    private SVNServiceUtils() {
    }

    public static SVNChangeLogRevision createChangeLogRevision(SVNRepository repository, String path, int level, long revision, String message, String author, LocalDateTime revisionDate) {
        // Issue service
        ConfiguredIssueService configuredIssueService = repository.getConfiguredIssueService();
        // Formatted message
        String formattedMessage;
        if (configuredIssueService != null) {
            formattedMessage = configuredIssueService.formatIssuesInMessage(message);
        } else {
            formattedMessage = message;
        }
        // Revision URL
        String revisionUrl = repository.getRevisionBrowsingURL(revision);
        // OK
        return new SVNChangeLogRevision(
                path,
                level,
                revision,
                author,
                revisionDate,
                message,
                revisionUrl,
                formattedMessage);
    }

}
