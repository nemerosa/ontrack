package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;
import org.springframework.stereotype.Component;

@Component
public class MaxCountBuildFilterProvider extends AbstractBuildFilterProvider {

    @Override
    protected Form blankForm() {
        return Form.create()
                .with(
                        Int.of("count")
                                .label("Maximum count")
                                .help("Maximum number of builds to display")
                                .min(1)
                );
    }

}
