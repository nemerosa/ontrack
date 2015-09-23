package net.nemerosa.ontrack.extension.svn.property;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLog;
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssueValidation;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import net.nemerosa.ontrack.extension.svn.db.SVNIssueRevisionDao;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLog;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLogIssue;
import net.nemerosa.ontrack.extension.svn.model.SVNHistory;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.MultiStrings;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SVNRevisionChangeLogIssueValidator extends AbstractSVNChangeLogIssueValidator<SVNRevisionChangeLogIssueValidatorConfig> {

    private final SVNIssueRevisionDao issueRevisionDao;

    @Autowired
    public SVNRevisionChangeLogIssueValidator(SVNExtensionFeature extensionFeature, PropertyService propertyService, SVNIssueRevisionDao issueRevisionDao) {
        super(extensionFeature, propertyService);
        this.issueRevisionDao = issueRevisionDao;
    }

    @Override
    public void validate(SCMChangeLog<SVNHistory> changeLog, SVNChangeLogIssue issue, SVNRevisionChangeLogIssueValidatorConfig validatorConfig) {
        SVNChangeLog svnChangeLog = (SVNChangeLog) changeLog;
        if (canApplyTo(svnChangeLog.getBranch())) {
            // Closed issue?
            if (validatorConfig.getClosedStatuses().contains(issue.getIssue().getStatus().getName())) {
                // Last revision for this issue
                OptionalLong lastRevision = issueRevisionDao.findLastRevisionByIssue(
                        svnChangeLog.getRepository().getId(),
                        issue.getIssue().getKey()
                );
                if (lastRevision.isPresent()) {
                    // Boundaries
                    long maxRevision = Math.max(
                            changeLog.getScmBuildFrom().getScm().getRevision(),
                            changeLog.getScmBuildTo().getScm().getRevision()
                    );
                    // Checks the boundaries
                    if (lastRevision.getAsLong() > maxRevision) {
                        issue.addValidations(
                                Collections.singletonList(
                                        SCMChangeLogIssueValidation.error(
                                                String.format(
                                                        "Issue %s is closed (%s), but has been fixed outside this " +
                                                                "change log",
                                                        issue.getIssue().getKey(),
                                                        issue.getIssue().getStatus().getName()
                                                )
                                        )
                                )
                        );
                    }
                }
            }
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
    public Form getEditionForm(ProjectEntity entity, SVNRevisionChangeLogIssueValidatorConfig value) {
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

    @Override
    public SVNRevisionChangeLogIssueValidatorConfig replaceValue(SVNRevisionChangeLogIssueValidatorConfig value, Function<String, String> replacementFunction) {
        return new SVNRevisionChangeLogIssueValidatorConfig(
                value.getClosedStatuses().stream()
                        .map(replacementFunction)
                        .collect(Collectors.toList())
        );
    }
}
