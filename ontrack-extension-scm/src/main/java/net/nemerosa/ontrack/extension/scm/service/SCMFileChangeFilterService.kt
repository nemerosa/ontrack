package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilter
import net.nemerosa.ontrack.extension.scm.model.SCMFileChangeFilters
import net.nemerosa.ontrack.model.structure.Project

interface SCMFileChangeFilterService {

    fun loadSCMFileChangeFilters(project: Project): SCMFileChangeFilters

    fun save(project: Project, filter: SCMFileChangeFilter)

    fun delete(project: Project, name: String)

}