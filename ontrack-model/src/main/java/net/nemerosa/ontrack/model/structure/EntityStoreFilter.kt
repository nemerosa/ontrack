package net.nemerosa.ontrack.model.structure

data class EntityStoreFilter(
    val jsonContext: String? = null,
    val jsonFilter: String? = null,
    val jsonFilterCriterias: Map<String, Any>? = emptyMap(),
)
