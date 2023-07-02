package net.nemerosa.ontrack.extension.av.postprocessing.mock

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessing
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingMissingConfigException
import net.nemerosa.ontrack.extension.scm.service.SCM
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.parse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC)
class MockPostProcessing(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractExtension(extensionFeature), PostProcessing<MockPostProcessingConfig> {

    override val id: String = "mock"

    override val name: String = "Mock post processing"

    override fun parseAndValidate(config: JsonNode?): MockPostProcessingConfig {
        return if (config != null && !config.isNull) {
            config.parse<MockPostProcessingConfig>()
        } else {
            throw PostProcessingMissingConfigException()
        }
    }

    override fun postProcessing(
        config: MockPostProcessingConfig,
        autoVersioningOrder: AutoVersioningOrder,
        repositoryURI: String,
        repository: String,
        upgradeBranch: String,
        scm: SCM,
    ) {
        scm.upload(
            scmBranch = upgradeBranch,
            commit = "-",
            path = "post-processing.properties",
            content = "postProcessingStamp = ${config.postProcessingStamp}".encodeToByteArray(),
            message = "-"
        )
    }
}