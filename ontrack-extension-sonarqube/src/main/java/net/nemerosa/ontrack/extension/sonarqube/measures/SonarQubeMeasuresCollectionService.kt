package net.nemerosa.ontrack.extension.sonarqube.measures

import net.nemerosa.ontrack.extension.sonarqube.property.SonarQubeProperty
import net.nemerosa.ontrack.model.structure.Build

interface SonarQubeMeasuresCollectionService {

    fun collect(build: Build, property: SonarQubeProperty)

}