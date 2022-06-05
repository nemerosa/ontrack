package net.nemerosa.ontrack.extension.av.postprocessing

import net.nemerosa.ontrack.extension.api.ExtensionManager
import org.springframework.stereotype.Service

@Service
class PostProcessingRegistryImpl(
    private val extensionManager: ExtensionManager,
) : PostProcessingRegistry {

    @Suppress("UNCHECKED_CAST")
    override fun <T> getPostProcessingById(id: String): PostProcessing<T>? =
        allPostProcessings.find { it.id == id } as? PostProcessing<T>?

    override val allPostProcessings: List<PostProcessing<*>> by lazy {
        extensionManager.getExtensions(PostProcessing::class.java).toList()
    }

}
