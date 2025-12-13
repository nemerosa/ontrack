package net.nemerosa.ontrack.model.exceptions

import net.nemerosa.ontrack.common.BaseException
import kotlin.reflect.KClass

class PropertyTypeStorageReadException(type: KClass<*>, e: Exception) : BaseException(
    e,
    "Could not parse JSON into type ${type.java.name}: ${e.message}"
)
