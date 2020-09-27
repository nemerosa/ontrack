package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.stash.model.BitbucketProject
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import java.time.LocalDateTime

class BitbucketClientImpl(
        configuration: StashConfiguration
) : BitbucketClient {

    override val projects: List<BitbucketProject>
        get() = template.getForObject("/rest/api/1.0/projects?limit=1000", ProjectsResponse::class.java)?.values
                ?: emptyList()

    override fun getRepositories(project: BitbucketProject): List<BitbucketRepository> =
            template.getForObject("/rest/api/1.0/projects/${project.key}/repos?limit=1000", RepositoriesResponse::class.java)
                    ?.values
                    ?.map {
                        BitbucketRepository(
                                project = project.key,
                                repository = it.slug
                        )
                    }
                    ?: emptyList()

    override fun getRepositoryLastModified(repo: BitbucketRepository): LocalDateTime? =
            template.getForObject("/rest/api/1.0/projects/${repo.project}/repos/${repo.repository}/last-modified?at=HEAD", RepositoryLastModifiedResponse::class.java)
                    ?.latestCommit?.authorTimestamp
                    ?.let { timestamp -> Time.from(timestamp) }

    private val template = RestTemplateBuilder()
            .rootUri(configuration.url)
            .basicAuthentication(
                    configuration.user,
                    configuration.password
            )
            .build()

    private class ProjectsResponse(
            val values: List<BitbucketProject>
    )

    private class RepositoriesResponse(
            val values: List<RepositoriesResponseItem>
    )

    private class RepositoriesResponseItem(
            @Suppress("unused") val id: Int,
            val slug: String
    )

    private class RepositoryLastModifiedResponse(
            val latestCommit: CommitResponse?
    )

    private class CommitResponse(
            val authorTimestamp: Long?
    )

}