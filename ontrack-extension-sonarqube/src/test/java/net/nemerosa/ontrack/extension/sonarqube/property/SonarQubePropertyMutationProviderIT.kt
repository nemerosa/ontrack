package net.nemerosa.ontrack.extension.sonarqube.property

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SonarQubePropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var sonarQubeConfigurationService: SonarQubeConfigurationService

    @Test
    fun `Setting the SonarQube project property using the project name`() {
        asAdmin {
            withDisabledConfigurationTest {
                val name = uid("sq_")
                val config = SonarQubeConfiguration(
                    name = name,
                    url = "https://$name.nemerosa.com",
                    password = "token",
                )
                sonarQubeConfigurationService.newConfiguration(config)
                project {
                    run(
                        """
                            mutation {
                                setProjectSonarQubeProperty(input: {
                                    project: "${this.name}",
                                    configuration: "${config.name}",
                                    key: "project.key",
                                    validationStamp: "sonarqube",
                                    measures: ["test"],
                                    override: false,
                                    branchModel: false,
                                    branchPattern: "develop",
                                    validationMetrics: true,
                                }) {
                                    errors {
                                        message
                                    }
                                }
                            }
                        """
                    ) { data ->
                        checkGraphQLUserErrors(data, "setSonarQubeProperty") {
                            assertNotNull(getProperty(this, SonarQubePropertyType::class.java)) { property ->
                                assertEquals(config.name, property.configuration.name)
                                assertEquals("project.key", property.key)
                                assertEquals("sonarqube", property.validationStamp)
                                assertEquals(listOf("test"), property.measures)
                                assertEquals(false, property.override)
                                assertEquals(false, property.branchModel)
                                assertEquals("develop", property.branchPattern)
                                assertEquals(true, property.validationMetrics)
                            }
                        }
                    }
                }
            }
        }
    }

}