package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.av.config.AutoApprovalMode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractAutoVersioningTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService

    protected fun Branch.setAutoVersioning(
        init: AutoVersioningSetup.() -> Unit,
    ) {
        val setup = AutoVersioningSetup()
        setup.init()
        autoVersioningConfigurationService.setupAutoVersioning(
            this,
            AutoVersioningConfig(
                setup.configurations
            )
        )
    }

    protected class AutoVersioningSetup {

        val configurations = mutableListOf<AutoVersioningSourceConfig>()

        fun autoVersioningConfig(
            init: AutoVersioningConfigSetup.() -> Unit,
        ) {
            val setup = AutoVersioningConfigSetup()
            setup.init()
            configurations += setup()
        }

    }

    protected class AutoVersioningConfigSetup {

        private var project: String? = null
        private var branch: String = "master"
        private var promotion: String = "IRON"

        fun sourceProject(value: String) {
            project = value
        }

        fun sourceBranch(value: String) {
            branch = value
        }

        fun sourcePromotion(value: String) {
            promotion = value
        }

        operator fun invoke() = AutoVersioningSourceConfig(
            sourceProject = project ?: throw IllegalStateException("Missing source project"),
            sourceBranch = branch,
            sourcePromotion = promotion,
            targetPath = "gradle.properties",
            targetRegex = "version = (.*)",
            targetProperty = null,
            targetPropertyRegex = null,
            targetPropertyType = null,
            autoApproval = null,
            upgradeBranchPattern = null,
            postProcessing = null,
            postProcessingConfig = null,
            validationStamp = null,
            autoApprovalMode = AutoApprovalMode.SCM
        )

    }

}