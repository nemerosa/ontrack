package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilterService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilter
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilterLink
import net.nemerosa.ontrack.model.structure.StructureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SCMOrphanDisablingServiceImpl(
    private val scmCatalogFilterService: SCMCatalogFilterService,
    private val structureService: StructureService,
) : SCMOrphanDisablingService {

    private val logger: Logger = LoggerFactory.getLogger(SCMOrphanDisablingServiceImpl::class.java)

    override fun disableOrphanProjects() {
        var empty = false
        var offset = 0
        while (!empty) {
            val entries = scmCatalogFilterService.findCatalogProjectEntries(
                SCMCatalogProjectFilter(
                    offset = offset,
                    size = 100,
                    link = SCMCatalogProjectFilterLink.ORPHAN,
                )
            )
            if (entries.isEmpty()) {
                empty = true
            } else {
                offset += entries.size
                entries.forEach { entry ->
                    entry.project?.apply {
                        if (!isDisabled) {
                            logger.debug("Disabling project $name after it's become orphan from its SCM.")
                            structureService.disableProject(this)
                        }
                    }
                }
            }
        }
    }

}