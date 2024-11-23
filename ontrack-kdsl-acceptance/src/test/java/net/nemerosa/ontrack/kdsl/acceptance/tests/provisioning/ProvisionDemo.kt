package net.nemerosa.ontrack.kdsl.acceptance.tests.provisioning

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.spec.admin.admin
import net.nemerosa.ontrack.kdsl.spec.extension.environments.environments
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
    }

    @Test
    @Disabled
    fun `Provision environments`() {
        // Cleanup
        ontrack.findProjectByName(PRODUCT_A)?.delete()
        ontrack.environments.findEnvironmentByName(ENV_STAGING)?.delete()
        ontrack.environments.findEnvironmentByName(ENV_PRODUCTION)?.delete()

        // Predefined promotion levels
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
        }

        // Project provisioning
        val project = ontrack.createProject(PRODUCT_A, "Project which can be deployed into an environment")
        val branch = project.branch("main") { this }
        val bronze = branch.promotion(BRONZE)
        val silver = branch.promotion(SILVER)
        val gold = branch.promotion(GOLD)

        // Creating the environments
        val envStaging = ontrack.environments.createEnvironment(ENV_STAGING, 100)
        val envProduction = ontrack.environments.createEnvironment(ENV_PRODUCTION, 200)

    }

}