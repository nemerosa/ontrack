package net.nemerosa.ontrack.extension.sonarqube.measures

open class SonarQubeMeasuresCollectionResult(
        val measures: Map<String, Double>?,
        val message: String?
) {
    val ok = measures != null && message.isNullOrBlank()

    companion object {
        fun ok(measures: Map<String, Double>) = SonarQubeMeasuresCollectionResult(measures, null)
        fun error(message: String) = SonarQubeMeasuresCollectionResult(null, message)
    }
}
