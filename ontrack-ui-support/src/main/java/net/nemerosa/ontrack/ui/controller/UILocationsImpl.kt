package net.nemerosa.ontrack.ui.controller

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class UILocationsImpl(
    private val ontrackConfigProperties: OntrackConfigProperties,
) : UILocations {

    override fun page(relativeURI: String): String =
        "${ontrackConfigProperties.url}/${relativeURI.trimStart('/')}"
}