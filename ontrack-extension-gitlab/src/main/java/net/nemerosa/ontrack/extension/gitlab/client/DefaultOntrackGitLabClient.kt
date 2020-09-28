package net.nemerosa.ontrack.extension.gitlab.client

import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper
import org.gitlab4j.api.GitLabApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.IOException

class DefaultOntrackGitLabClient(
        private val configuration: GitLabConfiguration
) : OntrackGitLabClient {

    private val logger: Logger = LoggerFactory.getLogger(OntrackGitLabClient::class.java)

    private val api: GitLabApi by lazy {
        val personalAccessToken = configuration.password
        val api = GitLabApi(configuration.url, personalAccessToken)
        api.setRequestTimeout(1000, 5000)
        api.setIgnoreCertificateErrors(configuration.isIgnoreSslCertificate)
        api
    }

    override fun getRepositories(): List<String> {
        logger.debug("[gitlab] Getting repository list")
        return try {
            api.projectApi.ownedProjects
                    .map { "${it.namespace}/${it.name}" }
        } catch (e: Exception) {
            throw OntrackGitLabClientException(e)
        }
    }

    override fun getIssue(repository: String, id: Int): GitLabIssueWrapper {
        return try {
            val issue = api.issuesApi.getIssue(repository, id)
            // Milestone URL
            var milestoneUrl: String? = null
            if (issue.milestone != null) {
                milestoneUrl = "${configuration.url}/projects/${repository}/milestones/${issue.milestone?.id}"
            }
            // OK
            GitLabIssueWrapper(issue, milestoneUrl)
        } catch (e: Exception) {
            throw OntrackGitLabClientException(e)
        }
    }

    override fun getPullRequest(repository: String, id: Int): GitPullRequest? {
        return try {
            try {
                val pr = api.mergeRequestApi.getMergeRequest(repository, id)
                GitPullRequest(
                        id,
                        "#$id",
                        pr.sourceBranch,
                        pr.targetBranch,
                        pr.title,
                        pr.state,
                        pr.webUrl
                )
            } catch (ignored: FileNotFoundException) {
                null
            }
        } catch (e: IOException) {
            throw OntrackGitLabClientException(e)
        }
    }

}