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

    fun type(typeName: String, typeInit: SearchIndexMappingFieldTypeBuilder.() -> Unit = {}) = SearchIndexMappingFieldTypeBuilder(typeName).apply { typeInit() }

    fun id(typeInit: SearchIndexMappingFieldTypeBuilder.() -> Unit = {}) = type("long", typeInit)
    fun keyword(typeInit: SearchIndexMappingFieldTypeBuilder.() -> Unit = {}) = type("keyword", typeInit)
    fun text(typeInit: SearchIndexMappingFieldTypeBuilder.() -> Unit = {}) = type("text", typeInit)

    fun createMapping() = SearchIndexMapping(
            fields = fields.map { it.createField() }
    )
}

@SearchIndexMappingMarker
class SearchIndexMappingFieldBuilder<T : SearchItem>(
        private val property: KProperty1<T, Any>
) {

    private val types = mutableListOf<SearchIndexMappingFieldType>()

    infix fun to(typeBuilder: SearchIndexMappingFieldTypeBuilder): SearchIndexMappingFieldBuilder<T> {
        types.add(typeBuilder.createType())
        return this
    }

    fun createField() = SearchIndexMappingField(
            name = property.name,
            types = types.toList()
    )

}

@SearchIndexMappingMarker
class SearchIndexMappingFieldTypeBuilder(private val typeName: String) {

    var index: Boolean? = null
    var scoreBoost: Double? = null

    fun createType(): SearchIndexMappingFieldType {
        return SearchIndexMappingFieldType(
                type = typeName,
                index = index,
                scoreBoost = scoreBoost
        )
    }
}

@DslMarker
annotation class SearchIndexMappingMarker
