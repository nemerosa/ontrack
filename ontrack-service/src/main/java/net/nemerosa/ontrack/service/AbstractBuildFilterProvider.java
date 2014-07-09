package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.BuildFilterForm;
import net.nemerosa.ontrack.model.structure.BuildFilterProvider;

public abstract class AbstractBuildFilterProvider implements BuildFilterProvider {

    @Override
    public BuildFilterForm newFilterForm() {
        return new BuildFilterForm(
                getClass(),
                blankForm()
        );
    }

    protected abstract Form blankForm();
}
