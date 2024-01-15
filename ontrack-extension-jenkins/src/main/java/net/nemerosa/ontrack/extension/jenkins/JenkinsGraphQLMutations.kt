package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.support.ConnectionResult
import org.springframework.stereotype.Component

@Component
class JenkinsGraphQLMutations(
    private val jenkinsConfigurationService: JenkinsConfigurationService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "testJenkinsConfiguration",
            description = "Tests a Jenkins configuration",
            input = TestJenkinsConfigurationInput::class,
            outputName = "connectionResult",
            outputDescription = "Result of the test",
            outputType = ConnectionResult::class,
        ) { input ->
            jenkinsConfigurationService.test(
                JenkinsConfiguration(
                    name = input.name,
                    url = input.url,
                    user = input.user,
                    password = input.password,
                )
            )
        },
        unitMutation<CreateJenkinsConfigurationInput>(
            name = "createJenkinsConfiguration",
            description = "Creates a Jenkins configuration",
        ) { input ->
            jenkinsConfigurationService.newConfiguration(
                JenkinsConfiguration(
                    name = input.name,
                    url = input.url,
                    user = input.user,
                    password = input.password,
                )
            )
        },
        unitMutation<UpdateJenkinsConfigurationInput>(
            name = "updateJenkinsConfiguration",
            description = "Updates a Jenkins configuration",
        ) { input ->
            jenkinsConfigurationService.updateConfiguration(
                input.name,
                JenkinsConfiguration(
                    name = input.name,
                    url = input.url,
                    user = input.user,
                    password = input.password,
                )
            )
        },
        unitMutation<DeleteJenkinsConfigurationInput>(
            name = "deleteJenkinsConfiguration",
            description = "Deletes a Jenkins configuration",
        ) { input ->
            jenkinsConfigurationService.deleteConfiguration(input.name)
        },
    )

    private data class TestJenkinsConfigurationInput(
        val name: String,
        val url: String,
        val user: String?,
        val password: String?,
    )

    private data class CreateJenkinsConfigurationInput(
        val name: String,
        val url: String,
        val user: String?,
        val password: String?,
    )

    private data class UpdateJenkinsConfigurationInput(
        val name: String,
        val url: String,
        val user: String?,
        val password: String?,
    )

    private data class DeleteJenkinsConfigurationInput(
        val name: String,
    )

}