package net.nemerosa.ontrack.git

import org.junit.Test

class GitRepositoryTest {

    @Test
    void getRepositoryId_1() {
        assert new GitRepository("git://github.com/nemerosa/ontrack.git", "", "").id == "git___github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_2() {
        assert new GitRepository("git@github.com:nemerosa/ontrack.git", "", "").id == "git_github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_3() {
        assert new GitRepository("https://github.com/nemerosa/ontrack.git", "", "").id == "https___github_com_nemerosa_ontrack_git";
    }

}