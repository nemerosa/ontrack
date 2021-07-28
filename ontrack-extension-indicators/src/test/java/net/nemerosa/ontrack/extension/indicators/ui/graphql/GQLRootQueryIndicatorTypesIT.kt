package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.AbstractIndicatorsTestSupport
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import kotlin.test.assertEquals

class GQLRootQueryIndicatorTypesIT : AbstractIndicatorsTestSupport() {

    @Test
    fun `List of types`() {
        clearIndicators()
        val source = source()
        val category1 = category(id = "cat-1", source = source)
        val type11 = category1.booleanType(id = "type-11", source = source)
        val type12 = category1.booleanType(id = "type-12", source = source)
        val category2 = category(id = "cat-2")
        val type21 = category2.booleanType(id = "type-21")
        val type22 = category2.booleanType(id = "type-22")

        val data = asAdmin {
            run("""{
                indicatorTypes {
                    types {
                        id
                        name
                        link
                        computed
                        source {
                            name
                            provider {
                                id
                                name
                            }
                        }
                        category {
                            id
                            name
                            source {
                                name
                                provider {
                                    id
                                    name
                                }
                            }
                        }
                        valueType {
                            id
                            name
                            feature {
                                id
                            }
                        }
                        valueConfig
                    }
                }
            }""")
        }

        val expected = TestUtils.resourceJson("/graphql-root-indicatorTypes-result.json")

        assertEquals(expected, data)

    }

    @Test
    fun `List of types filtered by ID`() {
        clearIndicators()
        val source = source()
        val category1 = category(id = "cat-1", source = source)
        val type11 = category1.booleanType(id = "type-11", source = source)
        val type12 = category1.booleanType(id = "type-12", source = source)
        val category2 = category(id = "cat-2")
        val type21 = category2.booleanType(id = "type-21")
        val type22 = category2.booleanType(id = "type-22")

        val data = asAdmin {
            run("""{
                indicatorTypes {
                    types(id: "${type11.id}") {
                        id
                        name
                        link
                        computed
                        source {
                            name
                            provider {
                                id
                                name
                            }
                        }
                        category {
                            id
                            name
                            source {
                                name
                                provider {
                                    id
                                    name
                                }
                            }
                        }
                        valueType {
                            id
                            name
                            feature {
                                id
                            }
                        }
                        valueConfig
                    }
                }
            }""")
        }

        val expected = TestUtils.resourceJson("/graphql-root-indicatorTypes-filter-id-result.json")

        assertEquals(expected, data)

    }

    @Test
    fun `List of types filtered by category`() {
        clearIndicators()
        val source = source()
        val category1 = category(id = "cat-1", source = source)
        val type11 = category1.booleanType(id = "type-11", source = source)
        val type12 = category1.booleanType(id = "type-12", source = source)
        val category2 = category(id = "cat-2")
        val type21 = category2.booleanType(id = "type-21")
        val type22 = category2.booleanType(id = "type-22")

        val data = asAdmin {
            run("""{
                indicatorTypes {
                    types(category: "cat-1") {
                        id
                        name
                        link
                        computed
                        source {
                            name
                            provider {
                                id
                                name
                            }
                        }
                        category {
                            id
                            name
                            source {
                                name
                                provider {
                                    id
                                    name
                                }
                            }
                        }
                        valueType {
                            id
                            name
                            feature {
                                id
                            }
                        }
                        valueConfig
                    }
                }
            }""")
        }

        val expected = TestUtils.resourceJson("/graphql-root-indicatorTypes-filter-category-result.json")

        assertEquals(expected, data)

    }

}