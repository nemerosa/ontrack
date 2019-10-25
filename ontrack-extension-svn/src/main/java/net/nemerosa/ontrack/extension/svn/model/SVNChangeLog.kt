package net.nemerosa.ontrack.extension.svn.model

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLog
import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

class SVNChangeLog(
        uuid: String,
        project: Project,
        @JsonIgnore // Not sent to the client
        val repository: SVNRepository,
        scmBuildFrom: SCMBuildView<SVNHistory>,
        scmBuildTo: SCMBuildView<SVNHistory>) : SCMChangeLog<SVNHistory>(uuid, project, scmBuildFrom, scmBuildTo) {

    @JsonIgnore // Not sent to the client
    var revisions: SVNChangeLogRevisions? = null
    @JsonIgnore // Not sent to the client
    var issues: SVNChangeLogIssues? = null
    @JsonIgnore // Not sent to the client
    var files: SVNChangeLogFiles? = null

    // Gets the two histories
    // Empty references?
    // Sort them from->to with 'to' having the highest revision
    // Indexation of the 'from' history using the paths
    // List of ranges to collect
    // For each reference on the 'to' history
    // Collects a range of revisions
    // Gets any 'from' reference
    // Adds this reference
    // OK
    val changeLogReferences: Collection<SVNChangeLogReference>
        @JsonIgnore
        get() {
            var historyFrom = scmBuildFrom.scm
            var historyTo = scmBuildTo.scm
            if (historyFrom.references.isEmpty() || historyTo.references.isEmpty()) {
                return emptyList()
            }
            run {
                val fromRevision = historyFrom.references[0].revision
                val toRevision = historyTo.references[0].revision
                if (toRevision < fromRevision) {
                    val tmp = historyTo
                    historyTo = historyFrom
                    historyFrom = tmp
                }
            }
            val historyFromIndex = historyFrom.references.associateBy { it.path }
            val references = mutableListOf<SVNChangeLogReference>()
            for (toReference in historyTo.references) {
                var toRevision = toReference.revision
                var fromRevision: Long = 0
                val fromReference = historyFromIndex[toReference.path]
                if (fromReference != null) {
                    fromRevision = fromReference.revision
                    if (fromRevision > toRevision) {
                        val t = toRevision
                        toRevision = fromRevision
                        fromRevision = t
                    }
                }
                references.add(SVNChangeLogReference(
                        toReference.path,
                        fromRevision,
                        toRevision
                ))
            }
            return references
        }

    /**
     * A SVN change log works always on the same branch.
     */
    val branch: Branch
        @JsonIgnore
        get() = from.build.branch

    fun withRevisions(revisions: SVNChangeLogRevisions): SVNChangeLog {
        this.revisions = revisions
        return this
    }

    fun withIssues(issues: SVNChangeLogIssues): SVNChangeLog {
        this.issues = issues
        return this
    }

    fun withFiles(files: SVNChangeLogFiles): SVNChangeLog {
        this.files = files
        return this
    }
}
