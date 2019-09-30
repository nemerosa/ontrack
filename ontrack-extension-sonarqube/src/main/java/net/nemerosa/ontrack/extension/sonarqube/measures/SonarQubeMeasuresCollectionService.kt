package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

interface SonarQubeMeasuresCollectionService {

    fun collect(project: Project, logger: (String) -> Unit)

    /**
     * Checks if the [build] is eligible for SonarQube collection.
     */
    fun matches(build: Build, property: SonarQubeProperty): Boolean

    fun collect(build: Build, property: SonarQubeProperty): Ack

    /**
     * Gets the SonarQube measures for a build.
     *
     * @param build Build to get the measures for
     * @return Existing measures or `null` if not found
     */
    fun getMeasures(build: Build): SonarQubeMeasures?

}