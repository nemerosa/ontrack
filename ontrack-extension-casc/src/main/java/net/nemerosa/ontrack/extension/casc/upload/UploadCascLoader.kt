package net.nemerosa.ontrack.extension.casc.upload

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import net.nemerosa.ontrack.extension.casc.CascLoader
import org.springframework.stereotype.Component

@Component
class UploadCascLoader(
    private val cascConfigurationProperties: CascConfigurationProperties,
    private val cascUploadService: CascUploadService,
) : CascLoader {

    override fun loadCascFragments(): List<String> =
        if (cascConfigurationProperties.upload.enabled) {
            listOfNotNull(
                cascUploadService.download()
            )
        } else {
            emptyList()
        }
}