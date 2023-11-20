package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Project

interface ProjectRepository {

    fun lastActiveProjects(): List<Project>

}