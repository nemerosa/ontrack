package net.nemerosa.ontrack.kdsl.acceptance.tests.provisioning

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.av.AutoVersioningUtils.waitForAutoVersioningCompletion
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.seconds
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.acceptance.tests.workflows.WorkflowTestSupport
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.admin.admin
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioningCheck
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.casc.casc
import net.nemerosa.ontrack.kdsl.spec.extension.environments.environments
import net.nemerosa.ontrack.kdsl.spec.extension.environments.startPipeline
import net.nemerosa.ontrack.kdsl.spec.extension.general.release
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.random.Random

class ProvisionDemo : AbstractACCDSLTestSupport() {

    companion object {
        const val PRODUCT_A = "productA"

        const val ENV_STAGING = "staging"
        const val ENV_PRODUCTION = "production"

        const val BRONZE = "BRONZE"
        const val SILVER = "SILVER"
        const val GOLD = "GOLD"
        const val DIAMOND = "DIAMOND"
    }

    @Test
    @Disabled
    fun `Branches and promotions`() {
        val ref = Time.now.minusDays(10)
        project {
            branch("main") {
                promotion(BRONZE)
                promotion(SILVER)
                promotion(GOLD)

                (1..9).forEach { no ->
                    build {
                        val buildTime = ref.plusDays(no.toLong())
                        updateCreationTime(buildTime)
                        release = "1.0.$no"
                        promote(BRONZE, dateTime = buildTime)
                        if (no % 2 == 0) {
                            promote(SILVER, dateTime = buildTime)
                        }
                        if (no % 4 == 0) {
                            promote(GOLD, dateTime = buildTime)
                        }
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    @Test
    @Disabled
    fun `Deep dependencies`() {
        val levels = 4
        val dependenciesMax = 3
        val buildCountMax = 4

        // Project node (branches will always be the same name in this scenario)
        data class ProjectNode(
            val level: Int,
            val no: Int,
            val dependencies: List<ProjectNode>,
        ) {

            val builds = mutableListOf<Build>()

            val lastBuild get() = builds.last()
            val beforeLastBuild get() = builds.dropLast(1).lastOrNull() ?: lastBuild

            fun populate() {
                project(name = uid("L$level-$no-")) {
                    val project = this
                    branch("main") {
                        dependencies.forEach { dependency ->
                            dependency.populate()
                        }

                        // Creates the builds
                        println("Project ${project.name}")
                        val buildCount = Random.nextInt(1, buildCountMax)
                        repeat(buildCount) { no ->
                            val buildName = uid("${project.name}-$no-")
                            build(buildName) {
                                println("  * Build $buildName created")
                                builds += this

                                // Each build must be link to at least one build of each dependency.
                                // If not on the last build, we always link to the N-1 th build of the dependency
                                // If on the last build, there is 50% change to link to N -1, of not to the Nth (last) one

                                dependencies.forEach { dependency ->
                                    // Last build
                                    if (no == buildCount - 1) {
                                        if (Random.nextBoolean()) {
                                            linkTo(dependency.beforeLastBuild)
                                        } else {
                                            linkTo(dependency.lastBuild)
                                        }
                                    }
                                    // Not the last build
                                    else {
                                        linkTo(dependency.beforeLastBuild)
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }

        // Project counts per levels
        val projectCounts = mutableMapOf<Int, AtomicInt>()

        // Project count for a given level
        fun projectCount(level: Int): Int {
            val counter = projectCounts.getOrPut(level) { AtomicInt(0) }
            return counter.fetchAndIncrement()
        }

        // Creating one project node
        fun createProjectNode(level: Int): ProjectNode {
            val dependencies = if (level > 0) {
                val dependencyCount = Random.nextInt(1, dependenciesMax)
                (0 until dependencyCount).map {
                    createProjectNode(level = level - 1)
                }
            } else {
                emptyList()
            }
            return ProjectNode(
                level = level,
                no = projectCount(level),
                dependencies = dependencies,
            )
        }

        // Building the hierarchy of projects, branches & builds first
        val root = createProjectNode(level = levels)
        root.populate()
    }

    // Accepted levels of promotions
    enum class TestPromotionLevel { BRONZE, SILVER, GOLD }

    @OptIn(ExperimentalAtomicApi::class)
    @Test
    @Disabled
    fun `Dependencies with auto-versioning`() {
        // Cleanup
        ontrack.autoVersioning.audit.purge()
        ontrack.projects().forEach {
            it.delete()
        }

        // Predefined promotion levels
        predefinedPromotionLevels()

        // Structure definition node
        data class StructureDefinitionNode(
            val name: String,
            val promotion: TestPromotionLevel,
            val maxBuilds: Int,
            val majorVersion: Int,
            val children: List<StructureDefinitionNode>,
        ) {

            private var branch: Branch? = null
            private var builds = mutableListOf<Build>()

            fun createProjectsAndBranches(processed: MutableSet<String> = mutableSetOf()) {
                if (name in processed) return
                // Creating the dependencies first
                children.forEach { child ->
                    // Creating the child and its own children
                    child.createProjectsAndBranches(processed)
                }
                // Creating the project & its branch
                withMockScmRepository(ontrack) {
                    repositoryFile(
                        path = "versions.properties",
                        branch = "main",
                        content = {
                            children.joinToString("\n") { child ->
                                "${child.name}.version=1.0"
                            }
                        },
                    )
                    project(name = name) {
                        branch("main") {
                            configuredForMockRepository("main")
                            this@StructureDefinitionNode.branch = this
                            // Promotions up its maximum possible one
                            TestPromotionLevel.entries.forEach { pl ->
                                if (pl <= promotion) promotion(name = pl.name)
                            }
                            // Auto-versioning from this branch to all the children
                            setAutoVersioningConfig(
                                configurations = children.map { child ->
                                    AutoVersioningSourceConfig(
                                        sourceProject = child.name,
                                        sourceBranch = "main",
                                        sourcePromotion = child.promotion.name,
                                        targetPath = "versions.properties",
                                        targetProperty = "${child.name}.version",
                                        validationStamp = "auto",
                                    )
                                }
                            )
                        }
                    }
                }
                // Done
                processed.add(name)
            }

            fun populateBuilds(processed: MutableSet<String> = mutableSetOf()) {
                if (name in processed) return

                // Populating the builds for each child
                children.forEach { child ->
                    child.populateBuilds(processed)
                }

                // Creates N builds, depending on the settings
                repeat(maxBuilds) { no ->
                    val consumerBranch = branch
                    consumerBranch?.build("$majorVersion.$no") {
                        builds += this
                        // Linking this build "no" to some of the dependencies
                        children.forEach { child ->
                            val childBranch = child.branch
                            if (childBranch != null) {
                                val childBuilds = child.builds
                                val childBuild = childBuilds.getOrNull(no) ?: childBuilds.last()
                                // Promoting the child build
                                childBuild.promote(promotion = child.promotion.name)
                                // Waiting for the AV process to be OK
                                waitForAutoVersioningCompletion(
                                    ontrack,
                                    initial = 100L,
                                    timeout = 2_000L,
                                    interval = 500L
                                )
                                // Checking that the build is linked
                                autoVersioningCheck()
                            }
                        }
                    }
                }

                processed += name
            }
        }

        // DSL
        class StructureDefinitionNodeBuilder(
            private val nodes: MutableMap<String, StructureDefinitionNode>,
            private val name: String,
            private val promotion: TestPromotionLevel,
            private val maxBuilds: Int,
            private val majorVersion: Int,
        ) {
            private val children = mutableListOf<StructureDefinitionNode>()
            fun node(
                name: String,
                promotion: TestPromotionLevel,
                maxBuilds: Int,
                majorVersion: Int,
                code: StructureDefinitionNodeBuilder.() -> Unit = {}
            ) {
                val existingNode = nodes[name]
                val node = if (existingNode != null) {
                    existingNode
                } else {
                    val builder = StructureDefinitionNodeBuilder(
                        nodes = nodes,
                        name = name,
                        promotion = promotion,
                        maxBuilds = maxBuilds,
                        majorVersion = majorVersion,
                    )
                    builder.code()
                    builder.build().apply {
                        nodes[name] = this
                    }
                }
                children += node
            }

            fun build() = StructureDefinitionNode(
                name = name,
                promotion = promotion,
                maxBuilds = maxBuilds,
                children = children,
                majorVersion = majorVersion,
            )
        }

        fun structure(
            name: String,
            code: StructureDefinitionNodeBuilder.() -> Unit = {},
        ): StructureDefinitionNode {
            val builder = StructureDefinitionNodeBuilder(
                nodes = mutableMapOf<String, StructureDefinitionNode>(),
                name = name,
                promotion = TestPromotionLevel.GOLD,
                maxBuilds = 4,
                majorVersion = 1,
            )
            builder.code()
            return builder.build()
        }

        // Defining the structure
        val root = structure("aggregator") {
            node("analysis", TestPromotionLevel.GOLD, 4, majorVersion = 4) {
                node("computing", TestPromotionLevel.SILVER, 3, majorVersion = 2) {
                    node("job-engine", TestPromotionLevel.BRONZE, 2, majorVersion = 1)
                }
            }
            node("workflows", TestPromotionLevel.GOLD, 4, majorVersion = 5) {
                node("scheduling", TestPromotionLevel.SILVER, 3, majorVersion = 2) {
                    node("job-engine", TestPromotionLevel.BRONZE, 2, majorVersion = 1)
                }
            }
            node("reporting", TestPromotionLevel.GOLD, 4, majorVersion = 2) {
                node("dashboards", TestPromotionLevel.SILVER, 3, majorVersion = 3) {
                    node("charts", TestPromotionLevel.BRONZE, 2, majorVersion = 1)
                    node("rendering", TestPromotionLevel.BRONZE, 2, majorVersion = 2)
                }
            }
            node("ui", TestPromotionLevel.GOLD, 4, majorVersion = 5)
        }

        // Creating the projects, layer per layer
        root.createProjectsAndBranches()
        root.populateBuilds()
    }

    @Test
    @Disabled
    fun `Acceptance, staging and production`() {
        // Cleanup
        ontrack.projects().forEach {
            it.delete()
        }
        ontrack.environments.list().forEach { it.delete() }

        // Predefined promotion levels
        predefinedPromotionLevels()

        // Project provisioning
        val project = ontrack.createProject(PRODUCT_A)
        val branch = project.branch("main") { this }
        val bronze = branch.promotion(BRONZE)
        branch.promotion(SILVER)
        branch.promotion(GOLD)
        branch.promotion(DIAMOND)

        // Another project
        val projectB = ontrack.createProject("ProjectB")
        projectB.branch("release-1.2") {
            val pl = promotion("CANDIDATE")
            build("1.2.0") {
                promote("CANDIDATE")
            }
        }

        // Creating a build with all the promotions
        val build1 = branch.build("1.0.0") {
            promote(BRONZE)
            promote(SILVER)
            promote(GOLD)
            promote(DIAMOND)
            this
        }

        // Creating a build with the 2 first promotions
        val build2 = branch.build("1.0.1") {
            promote(BRONZE)
            promote(SILVER)
            this
        }

        // Creating a build with the first promotion
        val build3 = branch.build("1.0.2") {
            promote(BRONZE)
            this
        }

        // Running some Casc
        // TODO Deployment workflow on staging-pilot
        ontrack.casc.uploadYaml(
            """
                ontrack:
                    config:
                        environments:
                            environments:
                                - name: staging-pilot
                                  order: 100
                                  tags:
                                    - release
                                    - staging
                                    - pilot
                                - name: staging-live
                                  order: 150
                                  tags:
                                    - release
                                    - staging
                                    - live
                                - name: acceptance-pilot
                                  order: 200
                                  tags:
                                    - release
                                    - acceptance
                                    - pilot
                                - name: acceptance-live
                                  order: 250
                                  tags:
                                    - release
                                    - acceptance
                                    - live
                                - name: production-pilot
                                  order: 300
                                  tags:
                                    - release
                                    - production
                                    - pilot
                                - name: production-demo
                                  order: 300
                                  tags:
                                    - release
                                    - production
                                    - demo
                                - name: production-live
                                  order: 350
                                  tags:
                                    - release
                                    - production
                                    - live
                            slots:
                                - project: $PRODUCT_A
                                  environments:
                                    - name: staging-pilot
                                      admissionRules:
                                        - ruleId: promotion
                                          ruleConfig:
                                            promotion: BRONZE
                                      workflows:
                                        - trigger: CANDIDATE
                                          name: Message
                                          nodes:
                                            - id: msg
                                              executorId: mock
                                              data:
                                                text: Test
                                        - trigger: RUNNING
                                          name: Deployment
                                          nodes:
                                            - id: done
                                              executorId: slot-pipeline-deployed
                                              data: {}
                                    - name: staging-live
                                      admissionRules:
                                        - ruleId: environment
                                          ruleConfig:
                                            environmentName: staging-pilot
                                    - name: acceptance-pilot
                                      admissionRules:
                                        - ruleId: promotion
                                          ruleConfig:
                                            promotion: GOLD
                                        - ruleId: environment
                                          ruleConfig:
                                            environmentName: staging-live
                                    - name: acceptance-live
                                      admissionRules:
                                        - ruleId: manual
                                          ruleConfig:
                                            message: Approval for acceptance live
                                        - ruleId: environment
                                          ruleConfig:
                                            environmentName: acceptance-pilot
                                    - name: production-pilot
                                      admissionRules:
                                        - ruleId: promotion
                                          ruleConfig:
                                            promotion: DIAMOND
                                        - ruleId: environment
                                          ruleConfig:
                                            environmentName: acceptance-live
                                    - name: production-demo
                                      admissionRules:
                                        - ruleId: promotion
                                          ruleConfig:
                                            promotion: DIAMOND
                                    - name: production-live
                                      admissionRules:
                                        - ruleId: manual
                                          ruleConfig:
                                            message: Approval for production live
                                        - ruleId: environment
                                          ruleConfig:
                                            environmentName: production-pilot
            """.trimIndent()
        )

        // Waiting until the provisioning of the slots
        waitUntil(
            task = "Provisioning of environments",
            timeout = 60.seconds,
            interval = 1.seconds,
        ) {
            ontrack.environments.findSlot("production-live", project.name) != null
        }

        // Getting the slots
        val slotStagingPilot = ontrack.environments.getSlot("staging-pilot", project.name)
        val slotStagingLive = ontrack.environments.getSlot("staging-live", project.name)
        val slotAcceptancePilot = ontrack.environments.getSlot("acceptance-pilot", project.name)
        val slotAcceptanceLive = ontrack.environments.getSlot("acceptance-live", project.name)
        val slotProductionPilot = ontrack.environments.getSlot("production-pilot", project.name)
        val slotProductionLive = ontrack.environments.getSlot("production-live", project.name)

        // Deploying 1.0.0 in all environments
        build1.startPipeline(slotStagingPilot).startDeploying().finishDeployment()

        build1.startPipeline(slotStagingLive).startDeploying().finishDeployment()

        build1.startPipeline(slotAcceptancePilot).startDeploying().finishDeployment()

        build1.startPipeline(slotAcceptanceLive)
            .manualApproval("OK for Acceptance Live")
            .startDeploying()
            .finishDeployment()

        build1.startPipeline(slotProductionPilot)
            .startDeploying()
            .finishDeployment()

        build1.startPipeline(slotProductionLive)
            .manualApproval("OK for Production Live")
            .startDeploying()
            .finishDeployment()

        // Workflow for deployment on BRONZE
        ontrack.notifications.subscribe(
            name = "Deployment to staging-pilot on BRONZE",
            channel = "workflow",
            channelConfig = mapOf(
                "workflow" to WorkflowTestSupport.yamlWorkflowToJson(
                    """
                        name: Deployment to staging-pilot
                        nodes:
                          - id: start
                            executorId: slot-pipeline-creation
                            data:
                              environment: staging-pilot
                          - id: deploy
                            parents:
                              - id: start
                            executorId: slot-pipeline-deploying
                            data: {}
                    """.trimIndent()
                )
            ),
            events = listOf("new_promotion_run"),
            projectEntity = bronze,
        )
    }

    @Test
    @Disabled
    fun `Provision environments`() {
        // Cleanup
        ontrack.findProjectByName(PRODUCT_A)?.delete()
        ontrack.environments.findEnvironmentByName(ENV_STAGING)?.delete()
        ontrack.environments.findEnvironmentByName(ENV_PRODUCTION)?.delete()

        // Predefined promotion levels
        predefinedPromotionLevels()

        // Project provisioning
        val project = ontrack.createProject(PRODUCT_A, "Project which can be deployed into an environment")
        val branch = project.branch("main") { this }
        branch.promotion(BRONZE)
        branch.promotion(SILVER)
        branch.promotion(GOLD)

        // Creating a build with all the promotions
        val build1 = branch.build("1.0.0") {
            promote(BRONZE)
            promote(SILVER)
            promote(GOLD)
            this
        }

        // Creating a build with the 2 first promotions
        val build2 = branch.build("1.0.1") {
            promote(BRONZE)
            promote(SILVER)
            this
        }

        // Creating a build with the first promotion
        val build3 = branch.build("1.0.2") {
            promote(BRONZE)
            this
        }

    }

    private fun predefinedPromotionLevels() {
        ontrack.admin.predefinedPromotionLevels.apply {
            createPredefinedPromotionLevel(
                name = BRONZE,
                image = ProvisionDemo::class.java.getResource("/promotions/bronze.png"),
                override = true,
            )
            createPredefinedPromotionLevel(
                name = SILVER,
                image = ProvisionDemo::class.java.getResource("/promotions/silver.png"),
                override = true,
            )
            createPredefinedPromotionLevel(
                name = GOLD,
                image = ProvisionDemo::class.java.getResource("/promotions/gold.png"),
                override = true,
            )
            createPredefinedPromotionLevel(
                name = DIAMOND,
                image = ProvisionDemo::class.java.getResource("/promotions/diamond.png"),
                override = true,
            )
        }
    }

}