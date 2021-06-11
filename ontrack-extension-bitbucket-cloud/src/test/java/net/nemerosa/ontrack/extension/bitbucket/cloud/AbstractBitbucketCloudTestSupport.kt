package net.nemerosa.ontrack.extension.bitbucket.cloud

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfigurationService
import net.nemerosa.ontrack.extension.bitbucket.cloud.property.BitbucketCloudProjectConfigurationProperty
import net.nemerosa.ontrack.extension.bitbucket.cloud.property.BitbucketCloudProjectConfigurationPropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractBitbucketCloudTestSupport : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var bitbucketCloudConfigurationService: BitbucketCloudConfigurationService

    fun Project.setBitbucketCloudProperty(
        config: BitbucketCloudConfiguration,
        repository: String,
        indexationInterval: Int = 0,
        issueServiceConfigurationIdentifier: String? = null,
    ) {
        setProperty(
            this,
            BitbucketCloudProjectConfigurationPropertyType::class.java,
            BitbucketCloudProjectConfigurationProperty(
                configuration = config,
                repository = repository,
                indexationInterval = indexationInterval,
                issueServiceConfigurationIdentifier = issueServiceConfigurationIdentifier,
            )
        )
    }

}