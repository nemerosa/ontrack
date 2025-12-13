package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.buildfilter.BuildFilterForm
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProvider
import net.nemerosa.ontrack.model.structure.ID

abstract class AbstractBuildFilterProvider<T> : BuildFilterProvider<T> {
    override fun newFilterForm(branchId: ID): BuildFilterForm {
        return BuildFilterForm(
            type = javaClass,
            typeName = name,
            isPredefined = isPredefined,
        )
    }

    override fun getFilterForm(branchId: ID, data: T): BuildFilterForm {
        return BuildFilterForm(
            type = javaClass,
            typeName = name,
            isPredefined = isPredefined,
        )
    }

}