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

    /**
     * If this method returns <code>true</code>, there is no need to configure the filter,
     * and the {@link #blankForm(net.nemerosa.ontrack.model.structure.ID)} method
     * should return an {@linkplain net.nemerosa.ontrack.model.form.Form#create() empty form}.
     */
    protected abstract boolean isPredefined();

    protected abstract Form blankForm(ID branchId);
}
