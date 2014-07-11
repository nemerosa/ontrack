package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.ID;

import java.util.Optional;

public abstract class AbstractPredefinedBuildFilterProvider extends AbstractBuildFilterProvider<Object> {

    @Override
    protected boolean isPredefined() {
        return true;
    }

    @Override
    protected Form blankForm(ID branchId) {
        return Form.create();
    }

    @Override
    protected Form fill(Form form, Object data) {
        return form;
    }

    @Override
    public Optional<Object> parse(JsonNode data) {
        return Optional.of(new Object());
    }

}
