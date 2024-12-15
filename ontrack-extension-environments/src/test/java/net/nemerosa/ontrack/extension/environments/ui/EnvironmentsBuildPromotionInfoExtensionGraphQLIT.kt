package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.promotions.EnvironmentsBuildPromotionInfoExtensionTestSupport
import net.nemerosa.ontrack.extension.environments.settings.EnvironmentsSettingsBuildDisplayOption
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentsBuildPromotionInfoExtensionGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var environmentsBuildPromotionInfoExtensionTestSupport: EnvironmentsBuildPromotionInfoExtensionTestSupport

    @Test
    fun `Getting the promotion info for a build with some promotions using HIGHEST option`() {
        environmentsBuildPromotionInfoExtensionTestSupport.withSetup(
            buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.HIGHEST,
        ) { test ->


            val (
                build,
                _,
                bronze,
                silver,
                gold,
                runBronze1,
                runBronze2,
                runSilver,
                _,
                _,
                _,
                eligibleSlotWithSilverPromotionRulePipeline,
            ) = test

            run(
                """
                    fragment BuildPromotionInfoItemDataContent on BuildPromotionInfoItemData {
                        __typename
                        ... on PromotionLevel {
                            id
                        }
                        ... on PromotionRun {
                            id
                        }
                        ... on SlotPipeline {
                            id
                        }
                        ... on EnvironmentBuildCount {
                            id
                            count
                        }
                    }
                    query BuildPromotionInfo {
                        build(id: ${build.id}) {
                            promotionInfo {
                                items {
                                    promotionLevel {
                                        name
                                    }
                                    data {
                                        ...BuildPromotionInfoItemDataContent
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->

                assertEquals(
                    mapOf(
                        "build" to mapOf(
                            "promotionInfo" to mapOf(
                                "items" to listOf(
                                    mapOf(
                                        "promotionLevel" to null,
                                        "data" to mapOf(
                                            "__typename" to "SlotPipeline",
                                            "id" to eligibleSlotWithSilverPromotionRulePipeline?.id
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to gold.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionLevel",
                                            "id" to gold.id.toString()
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to silver.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionRun",
                                            "id" to runSilver.id.toString()
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to bronze.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionRun",
                                            "id" to runBronze2.id.toString()
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to bronze.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionRun",
                                            "id" to runBronze1.id.toString()
                                        )
                                    ),
                                ),
                            )
                        )
                    ).asJson(),
                    data
                )

            }
        }
    }

    @Test
    fun `Getting the promotion info for a build with some promotions using COUNT option`() {
        environmentsBuildPromotionInfoExtensionTestSupport.withSetup(
            buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.COUNT,
        ) { test ->


            val (
                build,
                _,
                bronze,
                silver,
                gold,
                runBronze1,
                runBronze2,
                runSilver,
                _,
                _,
                _,
                _,
            ) = test

            run(
                """
                    fragment BuildPromotionInfoItemDataContent on BuildPromotionInfoItemData {
                        __typename
                        ... on PromotionLevel {
                            id
                        }
                        ... on PromotionRun {
                            id
                        }
                        ... on SlotPipeline {
                            id
                        }
                        ... on EnvironmentBuildCount {
                            id
                            count
                        }
                    }
                    query BuildPromotionInfo {
                        build(id: ${build.id}) {
                            promotionInfo {
                                items {
                                    promotionLevel {
                                        name
                                    }
                                    data {
                                        ...BuildPromotionInfoItemDataContent
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->

                assertEquals(
                    mapOf(
                        "build" to mapOf(
                            "promotionInfo" to mapOf(
                                "items" to listOf(
                                    mapOf(
                                        "promotionLevel" to null,
                                        "data" to mapOf(
                                            "__typename" to "EnvironmentBuildCount",
                                            "id" to "${build.id}",
                                            "count" to 2,
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to gold.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionLevel",
                                            "id" to gold.id.toString()
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to silver.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionRun",
                                            "id" to runSilver.id.toString()
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to bronze.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionRun",
                                            "id" to runBronze2.id.toString()
                                        )
                                    ),
                                    mapOf(
                                        "promotionLevel" to mapOf(
                                            "name" to bronze.name,
                                        ),
                                        "data" to mapOf(
                                            "__typename" to "PromotionRun",
                                            "id" to runBronze1.id.toString()
                                        )
                                    ),
                                ),
                            )
                        )
                    ).asJson(),
                    data
                )

            }
        }
    }
}