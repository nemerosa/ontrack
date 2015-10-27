package net.nemerosa.ontrack.extension.svn.client;

import net.nemerosa.ontrack.extension.svn.db.SVNEventDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration;
import net.nemerosa.ontrack.tx.TransactionService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SVNClientImplTest {

    private SVNClient client;

    @Before
    public void before() {
        SVNEventDao svnEventDao = mock(SVNEventDao.class);
        TransactionService transactionService = mock(TransactionService.class);
        client = new SVNClientImpl(svnEventDao, transactionService);
    }

    @Test
    public void isTrunkOrBranch_project_trunk() {
        assertTrue(client.isTrunkOrBranch(repository(), "/project/trunk"));
    }

    @Test
    public void isTrunkOrBranch_project_trunk_path() {
        assertFalse(client.isTrunkOrBranch(repository(), "/project/trunk/my/path"));
    }

    @Test
    public void isTrunkOrBranch_trunk() {
        assertTrue(client.isTrunkOrBranch(repository(), "/trunk"));
    }

    @Test
    public void isTrunkOrBranch_trunk_path() {
        assertFalse(client.isTrunkOrBranch(repository(), "/trunk/my/path"));
    }

    @Test
    public void isTrunkOrBranch_project_branch() {
        assertTrue(client.isTrunkOrBranch(repository(), "/project/branches/1.x"));
    }

    @Test
    public void isTrunkOrBranch_project_branch_path() {
        assertFalse(client.isTrunkOrBranch(repository(), "/project/branches/1.x/my/path"));
    }

    @Test
    public void isTrunkOrBranch_branch() {
        assertTrue(client.isTrunkOrBranch(repository(), "/branches/1.x"));
    }

    @Test
    public void isTrunkOrBranch_branch_path() {
        assertFalse(client.isTrunkOrBranch(repository(), "/branches/1.x/my/path"));
    }

    @Test
    public void isTrunkOrBranch_project_tag() {
        assertFalse(client.isTrunkOrBranch(repository(), "/project/tags/1.0"));
    }

    @Test
    public void isTrunkOrBranch_tag() {
        assertFalse(client.isTrunkOrBranch(repository(), "/tags/1.0"));
    }

    @Test
    public void isTagOrBranch_project_trunk() {
        assertFalse(client.isTagOrBranch(repository(), "/project/trunk"));
    }

    @Test
    public void isTagOrBranch_trunk() {
        assertFalse(client.isTagOrBranch(repository(), "/trunk"));
    }

    @Test
    public void isTagOrBranch_project_branch() {
        assertTrue(client.isTagOrBranch(repository(), "/project/branches/1.x"));
    }

    @Test
    public void isTagOrBranch_branch() {
        assertTrue(client.isTagOrBranch(repository(), "/branches/1.x"));
    }

    @Test
    public void isTagOrBranch_project_tag() {
        assertTrue(client.isTagOrBranch(repository(), "/project/tags/1.0"));
    }

    @Test
    public void isTagOrBranch_project_tag_path() {
        assertFalse(client.isTagOrBranch(repository(), "/project/tags/1.0/my/path"));
    }

    @Test
    public void isTagOrBranch_tag() {
        assertTrue(client.isTagOrBranch(repository(), "/tags/1.0"));
    }

    @Test
    public void isTagOrBranch_tag_path() {
        assertFalse(client.isTagOrBranch(repository(), "/tags/1.0/my/path"));
    }

    @Test
    public void isTag_project_trunk() {
        assertFalse(client.isTag(repository(), "/project/trunk"));
    }

    @Test
    public void isTag_trunk() {
        assertFalse(client.isTag(repository(), "/trunk"));
    }

    @Test
    public void isTag_project_branch() {
        assertFalse(client.isTag(repository(), "/project/branches/1.x"));
    }

    @Test
    public void isTag_branch() {
        assertFalse(client.isTag(repository(), "/branches/1.x"));
    }

    @Test
    public void isTag_project_tag() {
        assertTrue(client.isTag(repository(), "/project/tags/1.0"));
    }

    @Test
    public void isTag_project_tag_path() {
        assertFalse(client.isTag(repository(), "/project/tags/1.0/my/path"));
    }

    @Test
    public void isTag_tag() {
        assertTrue(client.isTag(repository(), "/tags/1.0"));
    }

    private SVNRepository repository() {
        return SVNRepository.of(
                1,
                new SVNConfiguration(
                        "test",
                        "http://repository",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        1,
                        ""
                ),
                null
        );
    }

}