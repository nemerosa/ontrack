package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.PropertyService;

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
        return Form.create();
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator.getEditionForm
    }

    @Override
    public SVNRevisionChangeLogIssueValidatorConfig fromClient(JsonNode node) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator.fromClient
        return null;
    }

    @Override
    public SVNRevisionChangeLogIssueValidatorConfig fromStorage(JsonNode node) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator.fromStorage
        return null;
    }

    @Override
    public String getSearchKey(SVNRevisionChangeLogIssueValidatorConfig value) {
        // FIXME Method net.nemerosa.ontrack.extension.svn.property.SVNRevisionChangeLogIssueValidator.getSearchKey
        return null;
    }
}
