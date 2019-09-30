package net.nemerosa.ontrack.extension.sonarqube.client

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration

interface SonarQubeClientFactory {

    fun getClient(configuration: SonarQubeConfiguration): SonarQubeClient

}