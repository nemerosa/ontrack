package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Decorator
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import java.util.*

interface DecorationExtension<T> : Extension, Decorator<T> {
    /**
     * Scope of the decorator
     * 
     * @return List of [net.nemerosa.ontrack.model.structure.ProjectEntityType] this decorator can apply to
     */
    fun getScope(): EnumSet<ProjectEntityType>
}
