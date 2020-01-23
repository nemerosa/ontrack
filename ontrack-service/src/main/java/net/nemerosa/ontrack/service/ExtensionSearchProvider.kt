package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.model.structure.SearchIndexer
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchResult
import org.springframework.stereotype.Component
import java.util.stream.Stream

/**
 * [net.nemerosa.ontrack.model.structure.SearchProvider] based on extensions.
 */
@Component
class ExtensionSearchProvider(
        private val extensionManager: ExtensionManager
) : SearchProvider {

    private val providers: List<SearchProvider> by lazy {
        extensionManager.getExtensions(SearchExtension::class.java).map {
            it.searchProvider
        }
    }

    protected fun providers(): Stream<SearchProvider> = providers.stream()

    override fun isTokenSearchable(token: String): Boolean {
        return providers.any { p: SearchProvider -> p.isTokenSearchable(token) }
    }

    override fun search(token: String): Collection<SearchResult> {
        return providers
                .filter { p: SearchProvider -> p.isTokenSearchable(token) }
                .flatMap { p: SearchProvider -> p.search(token) }
    }

    override fun getSearchIndexers(): Collection<SearchIndexer<*>> =
            providers.flatMap {
                it.searchIndexers
            }

}