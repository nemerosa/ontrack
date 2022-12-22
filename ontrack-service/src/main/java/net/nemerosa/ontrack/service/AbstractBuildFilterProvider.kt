package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterForm;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProvider;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.ID;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

public abstract class AbstractBuildFilterProvider<T> implements BuildFilterProvider<T> {

    @Override
    public BuildFilterForm newFilterForm(ID branchId) {
        return new BuildFilterForm(
                getClass(),
                getName(),
                isPredefined(),
                rootForm().append(blankForm(branchId))
        );
    }

    @Override
    public BuildFilterForm getFilterForm(ID branchId, T data) {
        return new BuildFilterForm(
                getClass(),
                getName(),
                isPredefined(),
                fill(rootForm().append(blankForm(branchId)), data)
        );
    }

    protected abstract Form fill(Form form, T data);

    private Form rootForm() {
        return Form.create().with(defaultNameField().optional());
    }

    protected abstract Form blankForm(ID branchId);
}
