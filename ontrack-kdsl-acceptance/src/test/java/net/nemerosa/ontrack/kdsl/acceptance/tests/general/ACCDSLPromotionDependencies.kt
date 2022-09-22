package net.nemerosa.ontrack.kdsl.acceptance.tests.general

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.connector.graphql.GraphQLClientException
import net.nemerosa.ontrack.kdsl.spec.extension.general.promotionDependencies
import org.junit.jupiter.api.Test
import org.springframework.web.client.HttpClientErrorException.BadRequest
import kotlin.test.assertFailsWith

class ACCDSLPromotionDependencies : AbstractACCDSLTestSupport() {

    @Test
    fun `Previous promotion condition check failure must be an input exception`() {
        project {
            branch {
                val silver = promotion(name = "SILVER")
                val gold = promotion(name = "GOLD").apply {
                    promotionDependencies = listOf(silver.name)
                }
                build {
                    // Tries to promote to GOLD
                    // This is forbidden because of its dependency to SILVER
                    assertFailsWith<GraphQLClientException> {
                        promote(gold.name)
                    }
                }
            }
        }
    }

    @Test
    fun `Previous promotion condition check failure must be an input exception when using the REST API`() {
        project {
            branch {
                val silver = promotion(name = "SILVER")
                val gold = promotion(name = "GOLD").apply {
                    promotionDependencies = listOf(silver.name)
                }
                build {
                    // Tries to promote to GOLD using the REST API
                    // This is forbidden because of its dependency to SILVER
                    assertFailsWith<BadRequest> {
                        ontrack.connector.post(
                            "/rest/structure/builds/${id}/promotionRun/create",
                            body = mapOf(
                                "promotionLevelId" to gold.id,
                            )
                        )
                    }
                }
            }
        }
    }

}