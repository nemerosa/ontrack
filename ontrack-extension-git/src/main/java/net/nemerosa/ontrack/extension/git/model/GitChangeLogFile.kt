package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFileChangeType
import org.apache.commons.lang3.StringUtils

class GitChangeLogFile(
        val changeType: SCMChangeLogFileChangeType,
        val oldPath: String,
        val newPath: String,
        val url: String
) : SCMChangeLogFile {

    override val path: String
        get() = if (StringUtils.isNotBlank(oldPath)) oldPath else newPath

    override val changeTypes: List<SCMChangeLogFileChangeType>
        get() = listOf(changeType)

    fun withUrl(url: String): GitChangeLogFile {
        return GitChangeLogFile(changeType, oldPath, newPath, url)
    }

    companion object {

        fun of(changeType: SCMChangeLogFileChangeType, path: String): GitChangeLogFile {
            return GitChangeLogFile(changeType, path, "", "")
        }

        fun of(changeType: SCMChangeLogFileChangeType, oldPath: String, newPath: String): GitChangeLogFile {
            return GitChangeLogFile(changeType, oldPath, newPath, "")
        }
    }
}
