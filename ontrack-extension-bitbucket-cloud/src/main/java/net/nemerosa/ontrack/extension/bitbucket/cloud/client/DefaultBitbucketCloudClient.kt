package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.model.*
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class DefaultBitbucketCloudClient(
    override val workspace: String,
    private val user: String,
    private val token: String,
) : BitbucketCloudClient {

    override val projects: List<BitbucketCloudProject>
        get() = get<BitbucketCloudProjectList>("/2.0/workspaces/$workspace/projects").values

    override val repositories: List<BitbucketCloudRepository>
        get() = paginate<BitbucketCloudRepository, BitbucketCloudRepositoryList> { page ->
            "/2.0/repositories/$workspace?page=$page"
        }

    override fun getRepositoryLastModified(repository: BitbucketCloudRepository): LocalDateTime? =
        LocalDateTime.parse(repository.updated_on, DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private inline fun <reified T, reified P : BitbucketCloudPaginatedList<T>> paginate(
        noinline path: (Int) -> String,
    ): List<T> = paginate(P::class, path)

    private fun <T, P : BitbucketCloudPaginatedList<T>> paginate(
        pageType: KClass<P>,
        path: (Int) -> String,
    ): List<T> {
        var page = 1
        var hasNext = true
        val results = mutableListOf<T>()
        while (hasNext) {
            // Gets the page
            val list = get(pageType, path(page))
            // Adding the values
            results.addAll(list.values)
            // Pagination
            if (list.next != null) {
                hasNext = true
                page++
            } else {
                hasNext = false
            }
        }
        return results
    }

    private inline fun <reified T : Any> get(path: String): T =
        get(T::class, path)

    private fun <T : Any> get(responseType: KClass<T>, path: String): T =
        template.getForObject(path, responseType.java) ?: throw BitbucketCloudNoResponseException(path)

    private val template: RestTemplate by lazy {
        RestTemplateBuilder()
            .rootUri("https://api.bitbucket.org")
            .basicAuthentication(
                user,
                token
            )
            .build()
    }
}