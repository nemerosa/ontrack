package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JenkinsGraphQLControllerIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var jenkinsConfigurationService: JenkinsConfigurationService

    @Autowired
    private lateinit var jenkinsTestSupport: JenkinsTestSupport

    @Test
    fun `Creating a Jenkins configuration with GraphQL`() {
        asAdmin {
            withDisabledConfigurationTest {
                val name = TestUtils.uid("jc")
                val url = "https://jenkins"
                run(
                    """
                        mutation {
                            createJenkinsConfiguration(input: {
                                name: "$name",
                                url: "$url",
                                user: "some-user",
                                password: "some-password"
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
                ) { data ->
                    checkGraphQLUserErrors(data, "createJenkinsConfiguration")
                    assertNotNull(jenkinsConfigurationService.findConfiguration(name), "Config has been found") {
                        assertEquals(url, it.url)
                    }
                }
            }
        }
    }

    @Test
    fun `Creating a Jenkins configuration with GraphQL cannot override an existing configuration`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                    mutation {
                        createJenkinsConfiguration(input: {
                            name: "${config.name}",
                            url: "some-other-url",
                            user: "some-other-user",
                            password: "some-other-password"
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                assertUserError(
                    data, "createJenkinsConfiguration",
                    message = "Configuration already exists with same name: ${config.name}"
                )
            }
        }
    }

    @Test
    fun `Updating a Jenkins configuration - the password is not required`() {
        jenkinsTestSupport.withConfig { config ->
            val url = "https://jenkins.nemerosa.net"
            run(
                """
                        mutation {
                            updateJenkinsConfiguration(input: {
                                name: "${config.name}",
                                url: "$url",
                                user: "${config.user}"
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
            ) { data ->
                checkGraphQLUserErrors(data, "saveJenkinsConfiguration")
                assertNotNull(jenkinsConfigurationService.findConfiguration(config.name), "Config has been found") {
                    assertEquals(url, it.url)
                    assertEquals(config.user, it.user)
                    assertEquals(config.password, it.password)
                }
            }
        }
    }

    @Test
    fun `Updating a Jenkins configuration - changing the password`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                        mutation {
                            updateJenkinsConfiguration(input: {
                                name: "${config.name}",
                                url: "${config.url}",
                                user: "${config.user}",
                                password: "new-password",
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
            ) { data ->
                checkGraphQLUserErrors(data, "saveJenkinsConfiguration")
                assertNotNull(jenkinsConfigurationService.findConfiguration(config.name), "Config has been found") {
                    assertEquals(config.url, it.url)
                    assertEquals(config.user, it.user)
                    assertEquals("new-password", it.password)
                }
            }
        }
    }

    @Test
    fun `Deleting a Jenkins configuration`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                        mutation {
                            deleteJenkinsConfiguration(input: {
                                name: "${config.name}"
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    """
            ) { data ->
                checkGraphQLUserErrors(data, "deleteJenkinsConfiguration")
                assertNull(jenkinsConfigurationService.findConfiguration(config.name), "Config has been deleted")
            }
        }
    }

    @Test
    fun `Getting the Jenkins configuration with GraphQL`() {
        jenkinsTestSupport.withConfig { config ->
            run(
                """
                        {
                            jenkinsConfigurations {
                                name
                                url
                                user
                            }
                        }
                    """
            ) { data ->
                val configurations = data.path("jenkinsConfigurations")
                val node = configurations.find { it.getRequiredTextField("name") == config.name }
                assertNotNull(node, "Jenkins config found") {
                    assertEquals(config.url, it.getRequiredTextField("url"))
                    assertEquals(config.user, it.getRequiredTextField("user"))
                }
            }
        }
    }

}