package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.config.model.*
import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLGitHubIngestionConfigBranchFieldContributorIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var configService: ConfigService

    @Test
    fun `Getting the ingestion configuration for a branch`() {
        project {
            branch {
                configService.saveConfig(
                    this, IngestionConfig(
                        steps = IngestionConfigSteps(
                            filter = FilterConfig(
                                includes = ".*",
                                excludes = "ontrack.*",
                            ),
                            mappings = listOf(
                                StepIngestionConfigValidation(
                                    name = "Publishing",
                                    validation = "docker-publication",
                                    validationPrefix = false,
                                    description = "Publication into the Docker repository",
                                )
                            ),
                        ),
                        jobs = IngestionConfigJobs(
                            filter = FilterConfig(
                                includes = ".*",
                                excludes = "ontrack.*",
                            ),
                            mappings = listOf(
                                JobIngestionConfigValidation(
                                    name = "build",
                                    validation = "build",
                                    description = "Main build",
                                )
                            )
                        ),
                    )
                )
                run(
                    """
                    {
                        branches(id: $id) {
                            gitHubIngestionConfig {
                                steps {
                                    filter {
                                        includes
                                        excludes
                                    }
                                    mappings {
                                        name
                                        validation
                                        validationPrefix
                                        description
                                    }
                                }
                                jobs {
                                    filter {
                                        includes
                                        excludes
                                    }
                                    mappings {
                                        name
                                        validation
                                        description
                                    }
                                }
                            }
                        }
                    }
                """
                ).let { data ->
                    assertEquals(
                        mapOf(
                            "branches" to listOf(
                                mapOf(
                                    "gitHubIngestionConfig" to mapOf(
                                        "steps" to mapOf(
                                            "filter" to mapOf(
                                                "includes" to ".*",
                                                "excludes" to "ontrack.*",
                                            ),
                                            "mappings" to listOf(
                                                mapOf(
                                                    "name" to "Publishing",
                                                    "validation" to "docker-publication",
                                                    "validationPrefix" to false,
                                                    "description" to "Publication into the Docker repository",
                                                ),
                                            )
                                        ),
                                        "jobs" to mapOf(
                                            "filter" to mapOf(
                                                "includes" to ".*",
                                                "excludes" to "ontrack.*",
                                            ),
                                            "mappings" to listOf(
                                                mapOf(
                                                    "name" to "build",
                                                    "validation" to "build",
                                                    "description" to "Main build",
                                                ),
                                            ),
                                        ),
                                    )
                                )
                            )
                        ).asJson(),
                        data
                    )
                }
            }
        }
    }

}