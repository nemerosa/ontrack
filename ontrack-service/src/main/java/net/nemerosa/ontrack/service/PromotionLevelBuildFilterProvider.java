package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.ID;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public BuildFilter filter(ID branchId, Map<String, String[]> parameters) {
        // FIXME Method net.nemerosa.ontrack.service.PromotionLevelBuildFilterProvider.filter
        return DefaultBuildFilter.INSTANCE;
    }

    @Override
    protected Form blankForm(ID branchId) {
        return Form.create();
    }

}
