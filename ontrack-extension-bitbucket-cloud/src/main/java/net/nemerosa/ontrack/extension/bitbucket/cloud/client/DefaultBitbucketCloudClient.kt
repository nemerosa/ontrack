package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.model.BitbucketCloudNoResponseException
import net.nemerosa.ontrack.extension.bitbucket.cloud.model.BitbucketCloudProject
import net.nemerosa.ontrack.extension.bitbucket.cloud.model.BitbucketCloudProjectList
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import kotlin.reflect.KClass

class DefaultBitbucketCloudClient(
    override val workspace: String,
    private val user: String,
    private val token: String,
) : BitbucketCloudClient {

    override val projects: List<BitbucketCloudProject>
        get() = get<BitbucketCloudProjectList>("/2.0/workspaces/$workspace/projects").values

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