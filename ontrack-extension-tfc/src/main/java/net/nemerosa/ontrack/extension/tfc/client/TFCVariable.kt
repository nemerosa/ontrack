package net.nemerosa.ontrack.extension.tfc.client

data class TFCVariable(
    val id: String?,
    val key: String,
    val value: String?,
    val sensitive: Boolean,
    val description: String?,
)
