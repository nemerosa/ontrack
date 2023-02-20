package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilter
import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilters
import net.nemerosa.ontrack.model.structure.EntityDataService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SCMFileChangeFilterServiceImpl(
    private val entityDataService: EntityDataService,
) : SCMFileChangeFilterService {

    override fun loadSCMFileChangeFilters(project: Project): SCMFileChangeFilters =
        entityDataService.retrieve(
            project,
            SCMFileChangeFilters::class.java.name,
            SCMFileChangeFilters::class.java
        ) ?: SCMFileChangeFilters.create()

    override fun save(project: Project, filter: SCMFileChangeFilter) {
        val config = loadSCMFileChangeFilters(project).run {
            save(filter)
        }
        // Saves the store back
        entityDataService.store(project, SCMFileChangeFilters::class.java.name, config)
    }

    override fun delete(project: Project, name: String) {
        entityDataService.withData(
            project,
            SCMFileChangeFilters::class.java.name,
            SCMFileChangeFilters::class.java
        ) { filters: SCMFileChangeFilters -> filters.remove(name) }
    }
}