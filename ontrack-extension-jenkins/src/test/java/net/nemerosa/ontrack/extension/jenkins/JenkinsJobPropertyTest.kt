package net.nemerosa.ontrack.extension.jenkins;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class JenkinsJobPropertyTest {

    @Test
    public void getUrlForAnOrganizationBranch() {
        JenkinsJobProperty property = new JenkinsJobProperty(
                new JenkinsConfiguration("Test", "https://host", "", ""),
                "organization/job/Repository/job/Branch"
        );
        assertEquals(
                "https://host/job/organization/job/Repository/job/Branch",
                property.getUrl()
        );
    }

    @Test
    public void getPathComponentsForSimpleJob() {
        JenkinsJobProperty property = new JenkinsJobProperty(
                new JenkinsConfiguration("Test", "http://jenkins", "", ""),
                "test"
        );
        assertEquals(Collections.singletonList("test"), property.getPathComponents());
    }

    @Test
    public void getPathComponentsForFolderJob() {
        JenkinsJobProperty property = new JenkinsJobProperty(
                new JenkinsConfiguration("Test", "http://jenkins", "", ""),
                "test/test-master/test-master-build"
        );
        assertEquals(Arrays.asList("test", "test-master", "test-master-build"), property.getPathComponents());
    }

    @Test
    public void getPathComponentsForCompleteFolderJob() {
        JenkinsJobProperty property = new JenkinsJobProperty(
                new JenkinsConfiguration("Test", "http://jenkins", "", ""),
                "test/job/test-master/job/test-master-build"
        );
        assertEquals(Arrays.asList("test", "test-master", "test-master-build"), property.getPathComponents());
    }

}
