package net.nemerosa.ontrack.extension.svn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLog;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;

import java.util.*;

@EqualsAndHashCode(callSuper = false)
@Data
public class SVNChangeLog extends SCMChangeLog<SVNHistory> {

    @JsonIgnore // Not sent to the client
    private SVNChangeLogRevisions revisions;
    @JsonIgnore // Not sent to the client
    private SVNChangeLogIssues issues;
    @JsonIgnore // Not sent to the client
    private SVNChangeLogFiles files;
    @JsonIgnore // Not sent to the client
    private final SVNRepository repository;

    public SVNChangeLog(
            String uuid,
            Project project,
            SVNRepository repository,
            SCMBuildView<SVNHistory> scmBuildFrom,
            SCMBuildView<SVNHistory> scmBuildTo) {
        super(uuid, project, scmBuildFrom, scmBuildTo);
        this.repository = repository;
    }

    public SVNChangeLog withRevisions(SVNChangeLogRevisions revisions) {
        this.revisions = revisions;
        return this;
    }

    public SVNChangeLog withIssues(SVNChangeLogIssues issues) {
        this.issues = issues;
        return this;
    }

    public SVNChangeLog withFiles(SVNChangeLogFiles files) {
        this.files = files;
        return this;
    }

    @JsonIgnore
    public Collection<SVNChangeLogReference> getChangeLogReferences() {

        // Gets the two histories
        SVNHistory historyFrom = getScmBuildFrom().getScm();
        SVNHistory historyTo = getScmBuildTo().getScm();

        // Empty references?
        if (historyFrom.getReferences().isEmpty() || historyTo.getReferences().isEmpty()) {
            return Collections.emptyList();
        }

        // Sort them from->to with 'to' having the highest revision
        {
            long fromRevision = historyFrom.getReferences().get(0).getRevision();
            long toRevision = historyTo.getReferences().get(0).getRevision();
            if (toRevision < fromRevision) {
                SVNHistory tmp = historyTo;
                historyTo = historyFrom;
                historyFrom = tmp;
            }
        }

        // Indexation of the 'from' history using the paths
        Map<String, SVNReference> historyFromIndex = Maps.uniqueIndex(
                historyFrom.getReferences(),
                SVNReference::getPath
        );

        // List of ranges to collect
        List<SVNChangeLogReference> references = new ArrayList<>();

        // For each reference on the 'to' history
        for (SVNReference toReference : historyTo.getReferences()) {
            // Collects a range of revisions
            long toRevision = toReference.getRevision();
            long fromRevision = 0;
            // Gets any 'from' reference
            SVNReference fromReference = historyFromIndex.get(toReference.getPath());
            if (fromReference != null) {
                fromRevision = fromReference.getRevision();
                if (fromRevision > toRevision) {
                    long t = toRevision;
                    toRevision = fromRevision;
                    fromRevision = t;
                }
            }
            // Adds this reference
            references.add(new SVNChangeLogReference(
                    toReference.getPath(),
                    fromRevision,
                    toRevision
            ));
        }

        // OK
        return references;
    }

    /**
     * A SVN change log works always on the same branch.
     */
    @JsonIgnore
    public Branch getBranch() {
        return getFrom().getBuild().getBranch();
    }
}
