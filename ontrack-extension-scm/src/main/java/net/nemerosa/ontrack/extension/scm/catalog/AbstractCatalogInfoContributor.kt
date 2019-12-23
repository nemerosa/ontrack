package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.extension.ExtensionFeature

abstract class AbstractCatalogInfoContributor<T>(extensionFeature: ExtensionFeature) : AbstractExtension(extensionFeature), CatalogInfoContributor<T>
