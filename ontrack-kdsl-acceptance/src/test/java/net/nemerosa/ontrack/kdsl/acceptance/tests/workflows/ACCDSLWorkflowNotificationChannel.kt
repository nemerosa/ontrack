package net.nemerosa.ontrack.kdsl.acceptance.tests.workflows

import net.nemerosa.ontrack.common.waitFor
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.ProjectEntityType
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.jenkins
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceNodeStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.WorkflowInstanceStatus
import net.nemerosa.ontrack.kdsl.spec.extension.workflows.workflows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ACCDSLWorkflowNotificationChannel : AbstractACCDSLWorkflowsTestSupport() {

    @Test
    fun `Notifications reusing their parent output`() {
        // Defining a workflow
        val prefixGroupName = uid("w-")
        val yaml = """
            name: $prefixGroupName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: in-memory
                    channelConfig:
                        group: "start-$prefixGroupName"
                        data: PRJ-123
                    template: |
                        Message for project ${'$'}{project}
                - id: end
                  executorId: notification
                  data:
                    channel: in-memory
                    channelConfig:
                        group: "end-$prefixGroupName"
                    template: |
                        Message for ${'$'}{workflow.start?path=result.data} in project ${'$'}{project}
                  parents:
                    - id: start
        """.trimIndent()

        project {
            // Subscribe to new branches
            val subName = uid("sub-")
            subscribe(
                name = subName,
                channel = "workflow",
                channelConfig = mapOf(
                    "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                ),
                keywords = null,
                events = listOf(
                    "new_branch",
                ),
            )
            // Creating a branch to trigger the workflow
            branch {}

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

            // Waits until the workflow is finished
            waitUntilWorkflowFinished(instanceId = instanceId)

            // We expect the messages

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val message = ontrack.notifications.inMemory.group("start-$prefixGroupName").firstOrNull()
                message?.trim() == "Message for project $name"
            }

            waitUntil(
                timeout = 30_000,
                interval = 500L,
            ) {
                val message = ontrack.notifications.inMemory.group("end-$prefixGroupName").firstOrNull()
                message?.trim() == "Message for PRJ-123 in project $name"
            }
        }
    }

    @Test
    fun `Workflow node must still contain the build URL in its output when the remote job fails`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: result
                              value: FAILURE
                            - name: waiting
                              value: 1000
        """.trimIndent()

        project {
            // Subscribe to new branches
            val subName = uid("sub-")
            subscribe(
                name = subName,
                channel = "workflow",
                channelConfig = mapOf(
                    "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                ),
                keywords = null,
                events = listOf(
                    "new_branch",
                ),
            )
            // Creating a branch to trigger the workflow
            branch {}

            // Gets the workflow instance ID from the output of the notification
            waitUntil(
                task = "Getting the workflow instance id",
                timeout = 30_000L,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

            // Waits until the workflow is finished
            val instance = waitUntilWorkflowFinished(
                instanceId = instanceId,
                returnInstanceOnError = true,
            )

            // Checks that the node is marked as success & contains the build URL
            assertEquals(WorkflowInstanceStatus.ERROR, instance.status)
            assertNotNull(instance.getWorkflowInstanceNode("start"), "Node accessed") { node ->
                assertTrue(!node.error.isNullOrBlank(), "There is an error")
                assertNotNull(node.output) { output ->
                    val buildUrl = output.path("result").path("buildUrl").asText()
                    assertTrue(buildUrl.isNotBlank(), "Build URL must not be blank")
                }
            }
        }
    }

    @Test
    fun `Workflow node contains the build URL while the node is running`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: waiting
                              value: 5000
        """.trimIndent()



        project {
            // Subscribe to new branches
            val subName = uid("sub-")
            subscribe(
                name = subName,
                channel = "workflow",
                channelConfig = mapOf(
                    "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                ),
                keywords = null,
                events = listOf(
                    "new_branch",
                ),
            )
            // Creating a branch to trigger the workflow
            branch {}

            // Gets the workflow instance ID from the output of the notification
            waitUntil(
                task = "Getting the workflow instance id",
                timeout = 30_000L,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

            // Checks that the node is marked as ongoing & contains the build URL
            waitUntil(interval = 500L, timeout = 2_000L, task = "Waiting for the build URL") {
                val instance = ontrack.workflows.workflowInstance(instanceId) ?: fail("Instance not found")
                val output = instance.getExecutionOutput("start") ?: return@waitUntil false
                val buildUrl = output.path("result").path("buildUrl").asText()
                !buildUrl.isNullOrBlank()
            }
            // Waiting for the workflow result
            val instance = waitUntilWorkflowFinished(instanceId)
            // Checks that the node is marked as success & contains the build URL
            assertNotNull(instance.getExecutionOutput("start"), "Output filled in") { output ->
                val buildUrl = output.path("result").path("buildUrl").asText()
                assertTrue(buildUrl.isNotBlank(), "Build URL must not be blank")
            }
        }

    }

    @Test
    fun `If a Jenkins sync job fails in a workflow node, the workflow must fail`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: result
                              value: FAILURE
                - id: end
                  executorId: mock
                  data:
                    text: Should not be called
                  parents:
                    - id: start
        """.trimIndent()

        project {
            branch {
                val subName = uid("sub-")
                val pl = promotion().apply {
                    subscribe(
                        name = subName,
                        channel = "workflow",
                        channelConfig = mapOf(
                            "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                        ),
                        keywords = null,
                        events = listOf(
                            "new_promotion_run",
                        ),
                    )
                }
                build {
                    // Promoting to trigger the worflow
                    promote(pl.name)

                    waitUntil(
                        timeout = 30_000,
                        interval = 500L,
                    ) {
                        val instanceId = getWorkflowInstanceId(subName)
                        instanceId.isNullOrBlank().not()
                    }

                    // Getting the instance ID
                    val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

                    // Waits until the workflow is finished
                    val instance = waitUntilWorkflowFinished(instanceId = instanceId, returnInstanceOnError = true)

                    // We expect the workflow to have failed
                    assertEquals(WorkflowInstanceStatus.ERROR, instance.status)

                    // Checking the node states
                    val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
                    assertEquals(WorkflowInstanceNodeStatus.ERROR, nodeStatuses["start"])
                    assertEquals(WorkflowInstanceNodeStatus.CANCELLED, nodeStatuses["end"])
                }
            }
        }
    }

    @Test
    fun `A workflow notification result is the result of its workflow`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )
        // Defining a workflow
        val workflowName = uid("w-")
        val yaml = """
            name: $workflowName
            nodes:
                - id: start
                  executorId: notification
                  data:
                    channel: mock-jenkins
                    channelConfig:
                        config: $jenkinsConfName
                        job: /mock/$job
                        callMode: SYNC
                        parameters:
                            - name: waiting
                              value: 5000
                            - name: result
                              value: FAILURE
        """.trimIndent()

        project {
            // Subscribe to new branches
            val subName = uid("sub-")
            subscribe(
                name = subName,
                channel = "workflow",
                channelConfig = mapOf(
                    "workflow" to WorkflowTestSupport.yamlWorkflowToJson(yaml)
                ),
                keywords = null,
                events = listOf(
                    "new_branch",
                ),
            )
            // Creating a branch to trigger the workflow
            branch {}

            // Gets the workflow instance ID from the output of the notification
            waitUntil(
                task = "Getting the workflow instance id",
                timeout = 30_000L,
                interval = 500L,
            ) {
                val instanceId = getWorkflowInstanceId(subName)
                instanceId.isNullOrBlank().not()
            }

            // Getting the instance ID
            val instanceId = getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

            // Getting the notification record & checks it's running
            waitUntil(
                task = "Getting the running notification",
                timeout = 2_000L,
                interval = 250L,
            ) {
                val record = getWorkflowNotificationRecord(subName)
                record?.result?.type == "ONGOING"
            }

            // Waiting until the workflow itself is finished (and in error)
            val instance = waitUntilWorkflowFinished(instanceId = instanceId, returnInstanceOnError = true)
            assertEquals(WorkflowInstanceStatus.ERROR, instance.status)

            // Checks the notification record again
            val record = getWorkflowNotificationRecord(subName)
            assertEquals("ERROR", record?.result?.type)
        }
    }

    @Test
    fun `Security context must be passed to the node executions`() {
        val group = uid("group-")
        val subName = uid("sub-")
        withNotGrantProjectViewToAll {
            withUser(
                globalRole = "ADMINISTRATOR"
            ) { user ->
                // Creating a dependency
                val dep = project {
                    branch {
                        build { this }
                    }
                }
                // Creating a parent project
                project {
                    branch {
                        val pl = promotion().apply {
                            // Registering a workflow for this promotion
                            subscribe(
                                name = subName,
                                channel = "workflow",
                                channelConfig = mapOf(
                                    "workflow" to WorkflowTestSupport.yamlWorkflowToJson(
                                        """
                                            name: On promotion
                                            nodes:
                                                - id: start
                                                  executorId: mock
                                                  data:
                                                    text: First node in workflow
                                                - id: notification
                                                  parents:
                                                    - id: start
                                                  executorId: notification
                                                  data:
                                                    channel: in-memory
                                                    channelConfig:
                                                        group: $group
                                                    template: |
                                                        User ${'$'}{#.user} - build linked to ${'$'}{build.linked?project=${dep.branch.project.name}&mode=auto}
                                                  
                                        """.trimIndent()
                                    )
                                ),
                                keywords = null,
                                events = listOf(
                                    "new_promotion_run",
                                ),
                            )
                        }
                        build {
                            // Linking to the dependency
                            linkTo(dep)
                            // Promoting to trigger the workflow
                            promote(pl.name)

                            waitUntil(
                                timeout = 30_000,
                                interval = 500L,
                            ) {
                                val instanceId = getWorkflowInstanceId(subName)
                                instanceId.isNullOrBlank().not()
                            }

                            // Getting the instance ID
                            val instanceId =
                                getWorkflowInstanceId(subName) ?: fail("Cannot get the workflow instance ID")

                            // Waits until the workflow is finished
                            val instance =
                                waitUntilWorkflowFinished(instanceId = instanceId, returnInstanceOnError = true)

                            // We expect the workflow to have failed
                            assertEquals(WorkflowInstanceStatus.SUCCESS, instance.status)

                            // Checking the node states
                            val nodeStatuses = instance.nodesExecutions.associate { it.id to it.status }
                            assertEquals(WorkflowInstanceNodeStatus.SUCCESS, nodeStatuses["start"])
                            assertEquals(WorkflowInstanceNodeStatus.SUCCESS, nodeStatuses["notification"])

                            // Checks that a notification was received
                            waitUntil(
                                timeout = 5_000,
                                interval = 500L,
                                task = "In-memory message"
                            ) {
                                val message = ontrack.notifications.inMemory.group(group)
                                    .firstOrNull()?.trim()
                                println("In-memory message: $message")
                                message == "User ${user.email} - build linked to ${dep.name}"
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Waiting for the promotion notifications to be completed and successful`() {
        val targetGroup = uid("tg-")
        project {
            branch {
                val targetPl = promotion("pl-target")
                val targetBuild = build("target") { this }

                targetPl.subscribe(
                    name = uid("t"),
                    channel = "workflow",
                    channelConfig = mapOf(
                        "workflow" to WorkflowTestSupport.yamlWorkflowToJson(
                            """
                                name: On target promotion
                                nodes:
                                    - id: promotion
                                      executorId: notification
                                      data:
                                        channel: in-memory
                                        channelConfig:
                                          group: $targetGroup
                                          waitMs: 2000
                            """.trimIndent()
                        )
                    ),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run",
                    ),
                )

                project {
                    branch {
                        val pl = promotion("pl-source")

                        pl.subscribe(
                            name = uid("s"),
                            channel = "workflow",
                            channelConfig = mapOf(
                                "workflow" to WorkflowTestSupport.yamlWorkflowToJson(
                                    """
                                        name: On source promotion
                                        nodes:
                                          - id: promotion
                                            executorId: notification
                                            timeout: 20
                                            interval: 1
                                            data:
                                              channel: yontrack-promotion
                                              channelConfig:
                                                project: ${targetBuild.branch.project.name}
                                                branch: ${targetBuild.branch.name}
                                                build: ${targetBuild.name}
                                                promotion: ${targetPl.name}
                                                waitForPromotion: true
                                                waitForPromotionTimeout: 10s
                                              template: Promotion from source ${pl.branch.project.name}
                                    """.trimIndent()
                                )
                            ),
                            keywords = null,
                            contentTemplate = $$"Promotion of ${build}",
                            events = listOf("new_promotion_run"),
                        )

                        build("source") {
                            val sourceRun = promote(pl.name)

                            // Checks that the target promotion has been set
                            waitFor(
                                message = "Waiting for the target promotion to be set",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                targetBuild.getPromotionRunsForPromotionLevel(targetPl.name).firstOrNull()
                            } until {
                                it.description == "Promotion from source ${pl.branch.project.name}"
                            }

                            // Waits until the Promotion notification is OK
                            waitFor(
                                message = "Waiting for the promotion notification to be completed",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.notificationRecords(
                                    channel = "workflow",
                                    projectEntityType = ProjectEntityType.PROMOTION_RUN,
                                    projectEntityId = sourceRun.id.toInt(),
                                ).firstOrNull()
                            } until { record ->
                                record.result.type == "OK"
                            }

                            // Checks that the target notification has been received
                            waitFor(
                                message = "Waiting for the target notification to be received",
                                interval = 500.milliseconds,
                                timeout = 10.seconds,
                            ) {
                                ontrack.notifications.inMemory.group(targetGroup).firstOrNull()
                            } until { record ->
                                record == "Build ${targetBuild.name} has been promoted to ${targetPl.name} for branch ${targetPl.branch.name} in ${targetPl.branch.project.name}."
                            }
                        }
                    }
                }
            }
        }
    }

}