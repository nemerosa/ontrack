package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.stereotype.Component;

/**
 * Gets each last build for each promotion level
 */
@Component
public class PromotionLevelBuildFilterProvider extends AbstractBuildFilterProvider {

    @Override
    public String getName() {
        return "Last per promotion level";
    }

    @Override
    protected Form blankForm(ID branchId) {
        return Form.create();
    }

}
