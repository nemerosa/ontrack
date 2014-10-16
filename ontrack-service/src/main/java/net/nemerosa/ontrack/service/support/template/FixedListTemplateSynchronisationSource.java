package net.nemerosa.ontrack.service.support.template;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.support.AbstractTemplateSynchronisationSource;
import org.springframework.stereotype.Component;

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
    public boolean isApplicable(Branch branch) {
        return true;
    }

    @Override
    public Form getForm(Branch branch) {
        return Form.create()
                .with(
                        MultiStrings.of("names")
                                .help("Fixed list of names")
                                .label("Names")
                );
    }

    @Override
    public List<String> getBranchNames(Branch branch, FixedListTemplateSynchronisationSourceConfig config) {
        return config.getNames();
    }

}
