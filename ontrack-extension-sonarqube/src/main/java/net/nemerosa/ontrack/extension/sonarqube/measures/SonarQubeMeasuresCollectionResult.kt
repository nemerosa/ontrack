package net.nemerosa.ontrack.extension.sonarqube.measures

open class SonarQubeMeasuresCollectionResult(
        val message: String?
) {
    val ok = message.isNullOrBlank()

    companion object {
        val ok = SonarQubeMeasuresCollectionResult(null)
        fun error(message: String) = SonarQubeMeasuresCollectionResult(message)
    }
}
