package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ConfigurationsMutationsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var jenkinsConfigurationService: JenkinsConfigurationService

    @Autowired
    private lateinit var jenkinsTestSupport: JenkinsTestSupport

    @Test
    fun `Creating a Jenkins configuration with the generic GraphQL mutation`() {
        asAdmin {
            withDisabledConfigurationTest {
                val name = TestUtils.uid("jc")
                val url = "https://jenkins"
                run(
                    """
                        mutation {
                            createConfiguration(input: {
                                type: "jenkins",
                                name: "$name",
                                data: {
                                    url: "$url",
                                    user: "some-user",
                                    password: "some-password"
                                }
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                ) { data ->
                    checkGraphQLUserErrors(data, "createConfiguration")
                    assertNotNull(jenkinsConfigurationService.findConfiguration(name), "Config has been found") {
                        assertEquals(url, it.url)
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a Jenkins configuration with the generic GraphQL mutation cannot override an existing configuration`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                    mutation {
                        createConfiguration(input: {
                            type: "jenkins",
                            name: "${config.name}",
                            data: {
                                url: "some-other-url",
                                user: "some-other-user",
                                password: "some-other-password"
                            }
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                assertUserError(
                    data, "createConfiguration",
                    message = "Configuration already exists with same name: ${config.name}"
                )
            }
        }
    }

    @Test
    fun `Updating a Jenkins configuration with the generic GraphQL mutation - the password is not required`() {
        jenkinsTestSupport.withConfig { config ->
            val url = "https://jenkins.nemerosa.net"
            run(
                """
                        mutation {
                            updateConfiguration(input: {
                                type: "jenkins",
                                name: "${config.name}",
                                data: {
                                    url: "$url",
                                    user: "${config.user}"
                                }
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
            ) { data ->
                checkGraphQLUserErrors(data, "updateConfiguration")
                assertNotNull(jenkinsConfigurationService.findConfiguration(config.name), "Config has been found") {
                    assertEquals(url, it.url)
                    assertEquals(config.user, it.user)
                    assertEquals(config.password, it.password)
                }
            }
        }
    }

    @Test
    fun `Updating a Jenkins configuration with the generic GraphQL mutation - changing the password`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                        mutation {
                            updateConfiguration(input: {
                                type: "jenkins",
                                name: "${config.name}",
                                data: {
                                    url: "${config.url}",
                                    user: "${config.user}",
                                    password: "new-password",
                                }
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
            ) { data ->
                checkGraphQLUserErrors(data, "updateConfiguration")
                assertNotNull(jenkinsConfigurationService.findConfiguration(config.name), "Config has been found") {
                    assertEquals(config.url, it.url)
                    assertEquals(config.user, it.user)
                    assertEquals("new-password", it.password)
                }
            }
        }
    }

    @Test
    fun `Deleting a Jenkins configuration using the generic GraphQL mutation`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                        mutation {
                            deleteConfiguration(input: {
                                type: "jenkins",
                                name: "${config.name}"
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
            ) { data ->
                checkGraphQLUserErrors(data, "deleteConfiguration")
                assertNull(jenkinsConfigurationService.findConfiguration(config.name), "Config has been deleted")
            }
        }
    }

}