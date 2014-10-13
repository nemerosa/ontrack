package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.support.AbstractTemplateSynchronisationSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FixedListTemplateSynchronisationSource extends AbstractTemplateSynchronisationSource<FixedListTemplateSynchronisationSourceConfig> {

    protected FixedListTemplateSynchronisationSource() {
        super(FixedListTemplateSynchronisationSourceConfig.class);
    }

    @Override
    public String getId() {
        return "fixed";
    }

    @Override
    public String getName() {
        return "Fixed list of names";
    }

    @Override
    public boolean isApplicable(Project project) {
        return true;
    }

    @Override
    public Form getForm(Project project) {
        return Form.create()
                .with(
                        MultiStrings.of("names")
                                .help("Fixed list of names")
                                .label("Names")
                );
    }

    @Override
    public List<String> getBranchNames(Project project, FixedListTemplateSynchronisationSourceConfig config) {
        return config.getNames();
    }

    @Override
    public FixedListTemplateSynchronisationSourceConfig getDefaultConfig(Project project) {
        return new FixedListTemplateSynchronisationSourceConfig(Collections.emptyList());
    }

}
