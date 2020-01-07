package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SCMCatalogFilterServiceImpl(
        private val scmCatalog: SCMCatalog,
        private val catalogLinkService: CatalogLinkService,
        private val securityService: SecurityService
) : SCMCatalogFilterService {

    override fun findCatalogEntries(filter: SCMCatalogFilter): List<SCMCatalogEntry> {
        securityService.checkGlobalFunction(SCMCatalogAccessFunction::class.java)
        val repositoryRegex = filter.repository?.takeIf { it.isNotBlank() }?.toRegex()
        return scmCatalog.catalogEntries.sorted().filter { entry ->
            filter.scm?.takeIf { it.isNotBlank() }?.let { entry.scm == it } ?: true
        }.filter { entry ->
            filter.config?.takeIf { it.isNotBlank() }?.let { entry.config == it } ?: true
        }.filter { entry ->
            repositoryRegex?.let { it.matches(entry.repository) } ?: true
        }.filter { entry ->
            when (filter.link) {
                SCMCatalogFilterLink.ALL -> true
                SCMCatalogFilterLink.LINKED -> isLinked(entry)
                SCMCatalogFilterLink.ORPHAN -> !isLinked(entry)
            }
        }.drop(filter.offset).take(filter.size).toList()
    }

    private fun isLinked(entry: SCMCatalogEntry): Boolean = catalogLinkService.isLinked(entry)

}