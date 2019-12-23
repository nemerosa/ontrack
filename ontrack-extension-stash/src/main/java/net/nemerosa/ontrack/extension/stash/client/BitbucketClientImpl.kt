package net.nemerosa.ontrack.extension.stash.client

import net.nemerosa.ontrack.extension.stash.model.BitbucketProject
import net.nemerosa.ontrack.extension.stash.model.BitbucketRepository
import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder

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

}