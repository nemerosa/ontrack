package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

interface SonarQubeMeasuresCollectionService {

    fun collect(project: Project, logger: (String) -> Unit)

    fun collect(build: Build, property: SonarQubeProperty)

    /**
     * Gets the SonarQube measures for a build.
     *
     * @param build Build to get the measures for
     * @return Existing measures or `null` if not found
     */
    fun getMeasures(build: Build): SonarQubeMeasures?

}