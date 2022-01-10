package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestJUnit4Support
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.*
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLGitHubIngestionConfigBranchFieldContributorIT : AbstractIngestionTestJUnit4Support() {

    @Autowired
    private lateinit var configService: ConfigService

    @Test
    fun `Getting the ingestion configuration for a branch`() {
        project {
            branch {
                configService.saveConfig(
                    this, IngestionConfig(
                        general = IngestionConfigGeneral(
                            skipJobs = false,
                        ),
                        steps = listOf(
                            StepConfig(
                                name = "Publishing",
                                validation = "docker-publication",
                                validationJobPrefix = false,
                                description = "Publication into the Docker repository",
                            ),
                        ),
                        jobs = listOf(
                            JobConfig(
                                name = "build",
                                validation = "build",
                                description = "Main build",
                            )
                        ),
                        jobsFilter = FilterConfig(
                            includes = ".*",
                            excludes = "ontrack.*",
                        ),
                        stepsFilter = FilterConfig(
                            includes = ".*",
                            excludes = "ontrack.*",
                        ),
                    )
                )
                run(
                    """
                    {
                        branches(id: $id) {
                            gitHubIngestionConfig {
                                general {
                                    skipJobs
                                }
                                steps {
                                    name
                                    validation
                                    validationJobPrefix
                                    description
                                }
                                jobs {
                                    name
                                    validation
                                    description
                                }
                                jobsFilter {
                                    includes
                                    excludes
                                }
                                stepsFilter {
                                    includes
                                    excludes
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
                                        "general" to mapOf(
                                            "skipJobs" to false,
                                        ),
                                        "steps" to listOf(
                                            mapOf(
                                                "name" to "Publishing",
                                                "validation" to "docker-publication",
                                                "validationJobPrefix" to false,
                                                "description" to "Publication into the Docker repository",
                                            ),
                                        ),
                                        "jobs" to listOf(
                                            mapOf(
                                                "name" to "build",
                                                "validation" to "build",
                                                "description" to "Main build",
                                            ),
                                        ),
                                        "jobsFilter" to mapOf(
                                            "includes" to ".*",
                                            "excludes" to "ontrack.*",
                                        ),
                                        "stepsFilter" to mapOf(
                                            "includes" to ".*",
                                            "excludes" to "ontrack.*",
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