package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractPropertyTypeIT : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var buildFilterService: BuildFilterService

}