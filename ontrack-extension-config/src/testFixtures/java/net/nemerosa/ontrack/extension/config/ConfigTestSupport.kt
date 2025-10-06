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
        ci: String? = DEFAULT_CI,
        scm: String? = DEFAULT_SCM,
        scmBranch: String = "release/5.1",
        extraEnv: Map<String, String> = emptyMap(),
        env: Map<String, String> = EnvFixtures.generic(scmBranch, extraEnv),
        code: (payload: JsonNode) -> Unit,
    ) {
        graphQLTestSupport.run(
            """
                mutation ConfigureBuild(
                    ${'$'}config: String!,
                    ${'$'}ci: String,
                    ${'$'}scm: String,
                    ${'$'}env: [CIEnv!]!,
                ) {
                    configureBuild(input: {
                        config: ${'$'}config,
                        ci: ${'$'}ci,
                        scm: ${'$'}scm,
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
                "ci" to ci,
                "scm" to scm,
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
        ci: String? = DEFAULT_CI,
        scm: String? = DEFAULT_SCM,
        projectName: String = "yontrack",
        scmBranch: String = "release/5.1",
        extraEnv: Map<String, String> = emptyMap(),
        env: Map<String, String> = EnvFixtures.generic(scmBranch, extraEnv),
        code: (project: Project, payload: JsonNode) -> Unit,
    ) =
        withConfig(yaml, ci=ci, scm=scm, scmBranch = scmBranch, env = env, extraEnv = extraEnv) { payload ->
            assertNotNull(
                structureService.findProjectByName(projectName).getOrNull(),
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

    companion object {
        const val DEFAULT_CI = "generic"
        const val DEFAULT_SCM = "mock"
    }

}