package net.nemerosa.ontrack.extension.git.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import net.nemerosa.ontrack.extension.scm.model.SCMBuildView
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLog
import net.nemerosa.ontrack.model.structure.Project

@JsonPropertyOrder(alphabetic = true)
class GitChangeLog(
        uuid: String,
        project: Project,
        scmBuildFrom: SCMBuildView<GitBuildInfo>,
        scmBuildTo: SCMBuildView<GitBuildInfo>,
        val isSyncError: Boolean
) : SCMChangeLog<GitBuildInfo>(uuid, project, scmBuildFrom, scmBuildTo) {

    @JsonIgnore // Not sent to the client
    var commits: GitChangeLogCommits? = null

    @JsonIgnore // Not sent to the client
    var issues: GitChangeLogIssues? = null

    @JsonIgnore // Not sent to the client
    var files: GitChangeLogFiles? = null

    fun loadCommits(loader: (GitChangeLog) -> GitChangeLogCommits): GitChangeLogCommits {
        return commits ?: run {
            val loadedCommits = loader(this)
            this.commits = loadedCommits
            loadedCommits
        }
    }

    fun withIssues(issues: GitChangeLogIssues): GitChangeLog {
        this.issues = issues
        return this
    }

    fun withFiles(files: GitChangeLogFiles): GitChangeLog {
        this.files = files
        return this
    }

}
