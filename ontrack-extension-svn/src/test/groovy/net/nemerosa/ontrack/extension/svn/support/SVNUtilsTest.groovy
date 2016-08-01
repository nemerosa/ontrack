package net.nemerosa.ontrack.extension.svn.support

import org.junit.Test

class SVNUtilsTest {

    @Test
    void 'File URL and trunk'() {
        String repoURL = 'file:///Users/test/repo/category'
        def url = SVNUtils.toURL(repoURL, '/project/trunk')
        assert url.toString() == 'file:///Users/test/repo/category/project/trunk'
    }

    @Test
    void 'File URL and branch'() {
        String repoURL = 'file:///Users/test/repo/category'
        def url = SVNUtils.toURL(repoURL, '/project/branches/feature')
        assert url.toString() == 'file:///Users/test/repo/category/project/branches/feature'
    }

}
