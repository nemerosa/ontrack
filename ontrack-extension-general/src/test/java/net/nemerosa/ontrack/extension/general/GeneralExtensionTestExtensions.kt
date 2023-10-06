package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

fun AbstractDSLTestSupport.autoValidationStampProperty(
    project: Project,
    autoCreate: Boolean = true,
    autoCreateIfNotPredefined: Boolean = false,
) {
    setProperty(
        project,
        AutoValidationStampPropertyType::class.java,
        AutoValidationStampProperty(
            isAutoCreate = autoCreate,
            isAutoCreateIfNotPredefined = autoCreateIfNotPredefined,
        )
    )
}

fun AbstractDSLTestSupport.autoPromotionLevelProperty(
    project: Project,
    autoCreate: Boolean = true,
) {
    setProperty(
        project,
        AutoPromotionLevelPropertyType::class.java,
        AutoPromotionLevelProperty(
            isAutoCreate = autoCreate,
        )
    )
}

fun AbstractDSLTestSupport.useLabel(
    project: Project,
    useLabel: Boolean = true,
) {
    setProperty(
        project,
        BuildLinkDisplayPropertyType::class.java,
        BuildLinkDisplayProperty(
            useLabel = useLabel,
        )
    )
}

/**
 * Release property
 */
fun AbstractDSLTestSupport.releaseProperty(
    build: Build,
    label: String,
) {
    setProperty(build, ReleasePropertyType::class.java, ReleaseProperty(label))
}

/**
 * Meta information
 */
fun AbstractDSLTestSupport.metaInfoProperty(
    build: Build,
    vararg items: MetaInfoPropertyItem,
) {
    setProperty(
        build,
        MetaInfoPropertyType::class.java,
        MetaInfoProperty(items.toList())
    )
}

fun metaInfoItem(
    name: String,
    value: String?,
    link: String? = null,
    category: String? = null,
) = MetaInfoPropertyItem(name, value, link, category)
