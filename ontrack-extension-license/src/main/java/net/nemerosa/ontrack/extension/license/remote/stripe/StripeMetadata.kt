package net.nemerosa.ontrack.extension.license.remote.stripe

object StripeMetadata {

    const val LICENSE_NAME = "license.name"
    const val LICENSE_PROJECTS = "license.projects"

}

fun StripeMetadataContainer.getMetadata(key: String): String =
    metadata[key]
        ?: error("Cannot find metadata key $key in $id.")

fun StripeMetadataContainer.getMetadataInt(key: String): Int =
    Integer.parseInt(getMetadata(key), 10)
