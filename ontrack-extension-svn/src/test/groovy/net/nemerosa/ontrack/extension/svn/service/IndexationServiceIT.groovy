package net.nemerosa.ontrack.extension.svn.service

import net.nemerosa.ontrack.extension.svn.db.SVNRepository
import net.nemerosa.ontrack.extension.svn.db.SVNRepositoryDao
import net.nemerosa.ontrack.extension.svn.db.SVNRevisionDao
import net.nemerosa.ontrack.extension.svn.support.SVNTestRepo
import net.nemerosa.ontrack.extension.svn.support.SVNTestUtils
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.apache.commons.io.FileUtils
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

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

    @Autowired
    private IndexationService indexationService

    @Autowired
    private SVNRepositoryDao repositoryDao

    @Autowired
    private SVNRevisionDao revisionDao

    @Test
    void 'Indexation of merge info'() {

        /**
         * Preparation of a SVN project with branch merged into the trunk
         */

        File wd = new File('build/work/IndexationServiceIT/IndexationOfMergeInfo')
        FileUtils.forceMkdir(wd)
        // Few commits on the trunk
        repo.mkdir 'IndexationOfMergeInfo/trunk', 'Trunk'
        (1..3).each { repo.mkdir "IndexationOfMergeInfo/trunk/$it", "$it" }
        // Creating the branch and add some commits to merge later
        repo.copy 'IndexationOfMergeInfo/trunk', 'IndexationOfMergeInfo/branches/MyBranch', 'MyBranch'
        (4..6).each { repo.mkdir "IndexationOfMergeInfo/branches/MyBranch/$it", "Branch $it" }
        // Few commits on the trunk
        (7..9).each { repo.mkdir "IndexationOfMergeInfo/trunk/$it", "$it" }
        // Merges the branch into the trunk (revision = 11)
        repo.merge wd, 'IndexationOfMergeInfo/branches/MyBranch', 'IndexationOfMergeInfo/trunk', 'Merge'

        /**
         * Definition of the repository
         */

        def configuration = SVNTestUtils.repository().configuration
        def repositoryId = repositoryDao.getOrCreateByName(configuration.name)
        def repository = SVNRepository.of(repositoryId, configuration, null)

        /**
         * Indexation of this repository
         */

        asUser().with(GlobalSettings).call {
            ((IndexationServiceImpl) indexationService).indexFromLatest(repository, { println it })
        }

        /**
         * Makes sure the merge is registered
         */

        (5..7).each {
            assert revisionDao.getMergesForRevision(repositoryId, it) == [12]
        }

    }

}
