package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.extension.stash.model.StashConfiguration
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationProperty
import net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType
import net.nemerosa.ontrack.extension.stash.service.StashConfigurationService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.stereotype.Component

@Component
class BitbucketServerTestSupport(
    private val stashConfigurationService: StashConfigurationService,
    private val propertyService: PropertyService,
) {


    fun withBitbucketServerConfig(
        name: String = uid("stash_"),
        code: (config: StashConfiguration) -> Unit
    ) {
        val config = StashConfiguration(
            name = name,
            url = "https://${name}",
            user = "user",
            password = "xxx",
            autoMergeUser = null,
            autoMergeToken = null,
        )
        stashConfigurationService.newConfiguration(config)
        code(config)
    }

    fun setStashProjectProperty(project: Project, config: StashConfiguration, stashProject: String, stashRepo: String) {
        propertyService.editProperty(
            project,
            StashProjectConfigurationPropertyType::class.java,
            StashProjectConfigurationProperty(
                configuration = config,
                project = stashProject,
                repository = stashRepo,
                indexationInterval = 0,
                issueServiceConfigurationIdentifier = null,
            )
        )
    }

}