package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.scm.SCMJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.util.stream.Stream

/**
 * Jobs used to collect information about projects linked to SCM catalog entries.
 */
@Component
class CatalogInfoJob(
        private val structureService: StructureService,
        private val catalogLinkService: CatalogLinkService,
        private val catalogInfoCollector: CatalogInfoCollector
) : JobOrchestratorSupplier {

    override fun collectJobRegistrations(): Stream<JobRegistration> =
            structureService.projectList
                    .filter { catalogLinkService.getSCMCatalogEntry(it) != null }
                    .map {
                        JobRegistration(
                                createCatalogInfoJob(it),
                                Schedule.EVERY_WEEK
                        )
                    }
                    .stream()

    private fun createCatalogInfoJob(project: Project) = object : Job {

        override fun isDisabled(): Boolean = project.isDisabled || catalogLinkService.getSCMCatalogEntry(project) == null

        override fun getKey(): JobKey =
                SCMJobs.category
                        .getType("scm-catalog-info").withName("SCM Catalog Info")
                        .getKey(project.name)

        override fun getDescription(): String = "SCM Catalog info collection for ${project.name}"

        override fun getTask() = JobRun { listener ->
            catalogInfoCollector.collectCatalogInfo(project) {
                listener.message(it)
            }
        }
    }

}