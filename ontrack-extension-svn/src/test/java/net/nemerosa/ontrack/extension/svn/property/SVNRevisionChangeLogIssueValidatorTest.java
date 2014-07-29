package net.nemerosa.ontrack.extension.svn.property;

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
        SVNRevisionChangeLogIssueValidator validator = new SVNRevisionChangeLogIssueValidator(propertyService);
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