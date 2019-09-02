package net.nemerosa.ontrack.extension.sonarqube.client

interface SonarQubeClient {

    /**
     * Server version
     */
    val serverVersion: String

    /**
     * System health
     */
    val systemHealth: String

}