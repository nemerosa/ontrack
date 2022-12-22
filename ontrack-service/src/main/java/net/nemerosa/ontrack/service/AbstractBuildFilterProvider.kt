package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.buildfilter.BuildFilterForm
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProvider
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Form.Companion.defaultNameField
import net.nemerosa.ontrack.model.structure.ID

abstract class AbstractBuildFilterProvider<T> : BuildFilterProvider<T> {
    override fun newFilterForm(branchId: ID): BuildFilterForm {
        return BuildFilterForm(
            javaClass,
            name,
            isPredefined,
            rootForm().append(blankForm(branchId))
        )
    }

    override fun getFilterForm(branchId: ID, data: T): BuildFilterForm {
        return BuildFilterForm(
            javaClass,
            name,
            isPredefined,
            fill(rootForm().append(blankForm(branchId)), data)
        )
    }

    protected abstract fun fill(form: Form, data: T): Form

    private fun rootForm(): Form {
        return create().with(defaultNameField().optional())
    }

    protected abstract fun blankForm(branchId: ID): Form
}