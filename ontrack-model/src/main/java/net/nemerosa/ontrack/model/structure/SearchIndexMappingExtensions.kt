package net.nemerosa.ontrack.model.structure

import kotlin.reflect.KProperty1

@SearchIndexMappingMarker
fun <T : SearchItem> indexMappings(code: SearchIndexMappingBuilder<T>.() -> Unit): SearchIndexMapping {
    val builder = SearchIndexMappingBuilder<T>()
    builder.code()
    return builder.createMapping()
}

@SearchIndexMappingMarker
class SearchIndexMappingBuilder<T : SearchItem> {

    private val fields = mutableListOf<SearchIndexMappingFieldBuilder<T>>()

    operator fun KProperty1<T, Any>.unaryPlus(): SearchIndexMappingFieldBuilder<T> {
        val builder = SearchIndexMappingFieldBuilder(this)
        fields.add(builder)
        return builder
    }

    fun createMapping() = SearchIndexMapping(
            fields = fields.map { it.createField() }
    )
}

@SearchIndexMappingMarker
class SearchIndexMappingFieldBuilder<T : SearchItem>(
        private val property: KProperty1<T, Any>
) {

    private val types = mutableListOf<SearchIndexMappingFieldTypeBuilder>()

    infix fun to(code: SearchIndexMappingFieldTypeBuilder.() -> Unit): SearchIndexMappingFieldBuilder<T> {
        val type = SearchIndexMappingFieldTypeBuilder()
        types.add(type)
        type.code()
        return this
    }

    fun createField() = SearchIndexMappingField(
            name = property.name,
            types = types.map { it.createType() }
    )

}

@SearchIndexMappingMarker
class SearchIndexMappingFieldTypeBuilder {

    var type: String? = null
    var index: Boolean? = null
    var scoreBoost: Double? = null

    fun createType(): SearchIndexMappingFieldType {
        return SearchIndexMappingFieldType(
                type = type,
                index = index,
                scoreBoost = scoreBoost
        )
    }
}

@DslMarker
annotation class SearchIndexMappingMarker
