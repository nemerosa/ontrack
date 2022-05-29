package net.nemerosa.ontrack.extension.github.ingestion.processing

data class IngestionEventProcessingResultDetails(
    val result: IngestionEventProcessingResult,
    val details: String?,
) {

    operator fun plus(other: IngestionEventProcessingResultDetails): IngestionEventProcessingResultDetails {
        val newDetails = if (this.details != null && other.details != null) {
            "${this.details} | ${other.details}"
        } else if (this.details == null) {
            other.details
        } else {
            null
        }
        return IngestionEventProcessingResultDetails(
            result = this.result + other.result,
            details = newDetails,
        )
    }

    companion object {

        val empty = IngestionEventProcessingResultDetails(
            IngestionEventProcessingResult.IGNORED,
            null
        )

        fun ignored(details: String) = IngestionEventProcessingResultDetails(
            IngestionEventProcessingResult.IGNORED,
            details
        )

        fun processed(details: String? = null) = IngestionEventProcessingResultDetails(
            IngestionEventProcessingResult.PROCESSED,
            details
        )
    }
}