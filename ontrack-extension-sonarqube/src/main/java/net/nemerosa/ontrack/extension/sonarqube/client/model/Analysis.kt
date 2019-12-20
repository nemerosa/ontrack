package net.nemerosa.ontrack.extension.sonarqube.client.model

data class Analysis(
        val date: String,
        val events: List<Event>
)
