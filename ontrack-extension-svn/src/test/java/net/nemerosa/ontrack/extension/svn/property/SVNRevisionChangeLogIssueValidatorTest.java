package net.nemerosa.ontrack.extension.svn.property;

import net.nemerosa.ontrack.extension.svn.db.SVNIssueRevisionDao;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SVNRevisionChangeLogIssueValidatorTest {

    @Test
    public void fromStorage() {
        PropertyService propertyService = mock(PropertyService.class);
        SVNIssueRevisionDao issueRevisionDao = mock(SVNIssueRevisionDao.class);
        SVNRevisionChangeLogIssueValidator validator = new SVNRevisionChangeLogIssueValidator(propertyService, issueRevisionDao);
        assertEquals(
                new SVNRevisionChangeLogIssueValidatorConfig(
                        Arrays.asList("Closed", "Resolved")
                ),
                validator.fromStorage(
                        JsonUtils.object()
                                .with("closedStatuses", JsonUtils.stringArray("Closed", "Resolved"))
                                .end()
                )
        );
    }

}