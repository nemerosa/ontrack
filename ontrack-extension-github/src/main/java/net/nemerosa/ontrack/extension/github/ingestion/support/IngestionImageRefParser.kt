package net.nemerosa.ontrack.extension.github.ingestion.support

object IngestionImageRefParser {

    fun parseRef(ref: String): IngestionImageRef =
        if (ref.contains(":")) {
            val protocol = ref.substringBefore(":")
            val path = ref.substringAfter(":")
            IngestionImageRef(protocol, path)
        } else {
            IngestionImageRef(defaultProtocol, ref)
        }

    const val defaultProtocol: String = "github"

}

data class IngestionImageRef(
    val protocol: String,
    val path: String,
)