package net.nemerosa.ontrack.extension.sonarqube.client.model

class Measure(
        val metric: String,
        val history: List<MeasureHistoryItem>
)
