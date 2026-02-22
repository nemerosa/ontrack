package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.model.structure.Project

interface ScmSearchIndexService {

    fun index(project: Project)

}