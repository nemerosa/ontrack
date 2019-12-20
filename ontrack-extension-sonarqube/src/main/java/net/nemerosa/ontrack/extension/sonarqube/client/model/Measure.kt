package net.nemerosa.ontrack.extension.sonarqube.client.model

data class Measure(
        val metric: String,
        val history: List<MeasureHistoryItem>
)
