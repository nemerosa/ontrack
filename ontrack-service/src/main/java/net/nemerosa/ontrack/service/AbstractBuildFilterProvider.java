package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterForm;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProvider;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.ID;

import static net.nemerosa.ontrack.model.form.Form.defaultNameField;

public abstract class AbstractBuildFilterProvider implements BuildFilterProvider {

    @Override
    public BuildFilterForm newFilterForm(ID branchId) {
        return new BuildFilterForm(
                getClass(),
                getName(),
                Form.create().with(defaultNameField().optional()).append(blankForm(branchId))
        );
    }

    protected abstract Form blankForm(ID branchId);
}
