package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.PropertyService;

import java.util.Collections;

public class SVNRevisionChangeLogIssueValidator extends AbstractSVNChangeLogIssueValidator<SVNRevisionChangeLogIssueValidatorConfig> {

    public SVNRevisionChangeLogIssueValidator(PropertyService propertyService) {
        super(propertyService);
    }

    @Override
    public void validate(Branch branch, SCMChangeLogIssue issue, SVNRevisionChangeLogIssueValidatorConfig validatorConfig) {
        if (canApplyTo(branch)) {
            // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator.validate
        }
    }

    @Override
    public String getName() {
        return "Validator: closed issues";
    }

    @Override
    public String getDescription() {
        return "Detects issues which are closed but with revisions outside of the revision log.";
    }

    @Override
    public Form getEditionForm(SVNRevisionChangeLogIssueValidatorConfig value) {
        return Form.create()
                .with(
                        MultiStrings.of("closedStatuses")
                                .label("Closed statuses")
                                .help("List of issue statuses that are considered as closed.")
                                .value(value != null ? value.getClosedStatuses() : Collections.emptyList())
                );
    }

    @Override
    public SVNRevisionChangeLogIssueValidatorConfig fromClient(JsonNode node) {
        return fromStorage(node);
    }

    @Override
    public SVNRevisionChangeLogIssueValidatorConfig fromStorage(JsonNode node) {
        return new SVNRevisionChangeLogIssueValidatorConfig(
                JsonUtils.getStringList(node, "closedStatuses")
        );
    }

    @Override
    public String getSearchKey(SVNRevisionChangeLogIssueValidatorConfig value) {
        return "";
    }
}
