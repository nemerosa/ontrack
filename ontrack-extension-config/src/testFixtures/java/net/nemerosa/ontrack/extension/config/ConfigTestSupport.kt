package net.nemerosa.ontrack.extension.config

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.graphql.GraphQLTestSupport
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertNotNull

@Component
class ConfigTestSupport(
    private val structureService: StructureService,
    private val graphQLTestSupport: GraphQLTestSupport,
) {

    fun withConfig(
        yaml: String,
        scmBranch: String = "release/5.1",
        env: Map<String, String> = mapOf(
            "PROJECT_NAME" to "yontrack",
            "BRANCH_NAME" to scmBranch,
            "BUILD_NUMBER" to "23",
            "BUILD_REVISION" to "abcd123",
            "VERSION" to "5.1.12",
        ),
        code: (payload: JsonNode) -> Unit,
    ) {
        graphQLTestSupport.run(
            """
                mutation ConfigureBuild(
                    ${'$'}config: String!,
                    ${'$'}env: [CIEnv!]!,
                ) {
                    configureBuild(input: {
                        config: ${'$'}config,
                        ci: "generic",
                        scm: "mock",
                        env: ${'$'}env,
                    }) {
                        errors {
                            message
                            exception
                        }
                        build {
                            id
                        }
                    }
                }
            """,
            mapOf(
                "config" to yaml,
                "env" to env.map { mapOf("name" to it.key, "value" to it.value) },
            )
        ) { data ->
            graphQLTestSupport.checkGraphQLUserErrors(data, "configureBuild") { payload ->
                code(payload)
            }
        }
    }

    fun withConfigAndProject(
        yaml: String,
        scmBranch: String = "release/5.1",
        code: (project: Project, payload: JsonNode) -> Unit,
    ) =
        withConfig(yaml, scmBranch = scmBranch) { payload ->
            assertNotNull(
                structureService.findProjectByName("yontrack").getOrNull(),
                "Project has been created"
            ) { project ->
                code(project, payload)
            }
        }

    fun withConfigAndBranch(
        yaml: String,
        scmBranch: String = "release/5.1",
        branch: String = NameDescription.escapeName(scmBranch),
        code: (branch: Branch, payload: JsonNode) -> Unit,
    ) =
        withConfigAndProject(yaml, scmBranch = scmBranch) { project, payload ->
            // Branch
            assertNotNull(
                structureService.findBranchByName(project.name, branch).getOrNull(),
                "Branch has been created"
            ) { branch ->
                code(branch, payload)
            }
        }

    fun withConfigAndBuild(
        yaml: String,
        scmBranch: String = "release/5.1",
        code: (build: Build, payload: JsonNode) -> Unit,
    ) =
        withConfigAndBranch(yaml, scmBranch = scmBranch) { branch, payload ->
            assertNotNull(
                structureService.getLastBuild(branch.id).getOrNull(),
                "Build has been created"
            ) { build ->
                code(build, payload)
            }
        }

}