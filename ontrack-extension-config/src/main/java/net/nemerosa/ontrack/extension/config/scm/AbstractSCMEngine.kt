package net.nemerosa.ontrack.extension.config.scm

import net.nemerosa.ontrack.model.structure.PropertyService

abstract class AbstractSCMEngine(
    protected val propertyService: PropertyService,
    final override val name: String,
) : SCMEngine