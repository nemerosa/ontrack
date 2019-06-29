package net.nemerosa.ontrack.extension.svn.db

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.extension.svn.support.SVNUtils
import org.tmatesoft.svn.core.SVNURL

class SVNRepository(
        val id: Int,
        val configuration: SVNConfiguration,
        val configuredIssueService: ConfiguredIssueService? = null
) {

    val branchPattern: String
        get() = ".*/branches/[^/]+"

    val tagPattern: String
        get() = ".*/tags/[^/]+"

    val rootUrl: SVNURL
        get() = SVNUtils.toURL(configuration.url)

    fun getUrl(path: String): String {
        return configuration.getUrl(path)
    }

    fun getRevisionBrowsingURL(revision: Long): String {
        return configuration.getRevisionBrowsingURL(revision)
    }

    fun getPathBrowsingURL(path: String): String {
        return configuration.getPathBrowsingURL(path)
    }

    fun getFileChangeBrowsingURL(path: String, revision: Long): String {
        return configuration.getFileChangeBrowsingURL(path, revision)
    }

    companion object {

        @JvmStatic
        fun of(id: Int, configuration: SVNConfiguration, configuredIssueService: ConfiguredIssueService): SVNRepository {
            return SVNRepository(id, configuration, configuredIssueService)
        }
    }
}
