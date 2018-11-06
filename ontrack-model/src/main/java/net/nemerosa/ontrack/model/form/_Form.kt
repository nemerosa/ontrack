package net.nemerosa.ontrack.model.form

import net.nemerosa.ontrack.model.structure.NameDescription

/**
 * Creates a [Selection] field from a list of enums.
 *
 * @param E Type of enum
 * @param name Name for this field
 * @return Field
 */
inline fun <reified E : Enum<E>> selection(name: String, displayName: (E) -> String): Selection =
        Selection.of(name)
                .items(
                        enumValues<E>().map {
                            NameDescription.nd(
                                    it.name,
                                    displayName(it)
                            )
                        }
                )
                .itemId("name")
                .itemName("description")
