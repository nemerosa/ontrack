package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.SearchExtension
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * [net.nemerosa.ontrack.model.structure.SearchProvider] based on extensions.
 */
@Component
class ExtensionSearchProvider(
        private val extensionManager: ExtensionManager
) : SearchProvider {

    protected fun providers(): Stream<SearchProvider> {
        return extensionManager.getExtensions(SearchExtension::class.java).stream().map { obj: SearchExtension -> obj.searchProvider }
    }

    override fun isTokenSearchable(token: String): Boolean {
        return providers().anyMatch { p: SearchProvider -> p.isTokenSearchable(token) }
    }

    override fun search(token: String): Collection<SearchResult> {
        return providers()
                .filter { p: SearchProvider -> p.isTokenSearchable(token) }
                .flatMap { p: SearchProvider -> p.search(token).stream() }
                .collect(Collectors.toList())
    }

}