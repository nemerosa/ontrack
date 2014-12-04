package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import org.apache.commons.io.FileUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

/**
 * Indexation service integration tests.
 */
class IndexationServiceIT extends AbstractServiceTestSupport {

    private static SVNTestRepo repo

    @BeforeClass
    static void 'SVN repository: start'() {
        repo = SVNTestRepo.get('IndexationServiceIT')
    }

    @AfterClass
    static void 'SVN repository: stop'() {
        repo.stop()
    }

    @Test
    void 'Indexation of merge info'() {
        File wd = new File('build/work/IndexationServiceIT/IndexationOfMergeInfo')
        FileUtils.forceMkdir(wd)
        // Few commits on the trunk
        repo.mkdir 'IndexationOfMergeInfo/trunk', 'Trunk'
        (1..3).each { repo.mkdir "IndexationOfMergeInfo/trunk/$it", "$it" }
        // Creating the branch and add some commits to merge later
        repo.mkdir 'IndexationOfMergeInfo/branches/MyBranch', 'MyBranch'
        (4..6).each { repo.mkdir "IndexationOfMergeInfo/branches/MyBranch/$it", "Branch $it" }
        // Few commits on the trunk
        (7..9).each { repo.mkdir "IndexationOfMergeInfo/trunk/$it", "$it" }
        // Merges the branch into the trunk
        repo.merge wd, 'IndexationOfMergeInfo/branches/MyBranch', 'IndexationOfMergeInfo/trunk', 'Merge'
    }

}
