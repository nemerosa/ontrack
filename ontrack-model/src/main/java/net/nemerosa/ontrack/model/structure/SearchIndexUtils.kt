package net.nemerosa.ontrack.model.structure

import co.elastic.clients.elasticsearch._types.analysis.TokenChar
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest
import kotlin.reflect.KProperty1

const val AUTOCOMPLETE_ANALYSER = "autocomplete"
const val AUTOCOMPLETE_SEARCH_ANALYSER = "autocomplete_search"
const val AUTOCOMPLETE_TOKENIZER = "autocomplete_tokenizer"

fun CreateIndexRequest.Builder.autoCompleteSettings(): CreateIndexRequest.Builder =
    settings { settings ->
        settings.analysis { analysis ->
            analysis.analyzer(AUTOCOMPLETE_ANALYSER) { analyzer ->
                analyzer
                    .custom { custom ->
                        custom
                            .tokenizer(AUTOCOMPLETE_TOKENIZER)
                            .filter(listOf("lowercase"))
                    }
            }
                .analyzer(AUTOCOMPLETE_SEARCH_ANALYSER) { analyzer ->
                    analyzer
                        .custom { custom ->
                            custom
                                .tokenizer("standard")
                                .filter(listOf("lowercase"))
                        }
                }
                .tokenizer(AUTOCOMPLETE_TOKENIZER) { tokenizer ->
                    tokenizer
                        .definition { definition ->
                            definition
                                .edgeNgram { edgeNgram ->
                                    edgeNgram
                                        .minGram(3)
                                        .maxGram(50)
                                        .tokenChars(
                                            listOf(
                                                TokenChar.Letter,
                                                TokenChar.Digit,
                                                TokenChar.Punctuation,
                                                TokenChar.Whitespace
                                            )
                                        )
                                }
                        }
                }
        }
    }

fun TypeMapping.Builder.id(property: KProperty1<*, Int>): TypeMapping.Builder =
    properties(property.name) { property ->
        property.long_ {
            it.index(false)
        }
    }

fun TypeMapping.Builder.keyword(property: KProperty1<*, String>): TypeMapping.Builder =
    properties(property.name) { property ->
        property.keyword { it }
    }

fun TypeMapping.Builder.keywordAndText(property: KProperty1<*, String>): TypeMapping.Builder =
    properties(property.name) { property ->
        property.keyword { keyword ->
            keyword.fields("text") { field ->
                field.text { it }
            }
        }
    }

fun TypeMapping.Builder.text(property: KProperty1<*, String>): TypeMapping.Builder =
    properties(property.name) { property ->
        property.text { it }
    }

fun TypeMapping.Builder.autoCompleteText(property: KProperty1<*, String>): TypeMapping.Builder =
    properties(property.name) { property ->
        property
            .text { text ->
                text
                    .analyzer(AUTOCOMPLETE_ANALYSER)
                    .searchAnalyzer(AUTOCOMPLETE_SEARCH_ANALYSER)
            }
    }

fun MultiMatchQuery.Builder.fields(vararg fields: Pair<KProperty1<*, *>, Double?>): MultiMatchQuery.Builder {
    val fieldList = fields.map { (field, boost) ->
        if (boost != null) {
            "${field.name}^$boost"
        } else {
            field.name
        }
    }
    return fields(fieldList)
}