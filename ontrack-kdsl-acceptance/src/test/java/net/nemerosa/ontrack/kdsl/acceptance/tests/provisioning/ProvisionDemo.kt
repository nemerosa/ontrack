package net.nemerosa.ontrack.kdsl.acceptance.tests.provisioning

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.seconds
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.acceptance.tests.workflows.WorkflowTestSupport
import net.nemerosa.ontrack.kdsl.spec.admin.admin
import net.nemerosa.ontrack.kdsl.spec.extension.casc.casc
import net.nemerosa.ontrack.kdsl.spec.extension.environments.environments
import net.nemerosa.ontrack.kdsl.spec.extension.environments.startPipeline
import net.nemerosa.ontrack.kdsl.spec.extension.notifications.notifications
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

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
                                        - trigger: DEPLOYING
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