package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.Build

abstract class AbstractGeneralExtensionTestSupport : AbstractQLKTITSupport() {

    /**
     * Release property
     */
    @Deprecated("Use the AbstractDSLTestSupport.releaseProperty extension instead")
    protected var Build.releaseProperty: String?
        get() = property(ReleasePropertyType::class)?.name
        set(value) = if (value != null) {
            property(ReleasePropertyType::class, ReleaseProperty(value))
        } else {
            property(ReleasePropertyType::class, null)
        }

}