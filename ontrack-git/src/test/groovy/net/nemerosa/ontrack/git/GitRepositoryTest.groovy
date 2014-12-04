package net.nemerosa.ontrack.git

import org.junit.Test

class GitRepositoryTest {

    @Test
    void getRepositoryId_1() {
        assert new GitRepository("ontrack", "git://github.com/nemerosa/ontrack.git", "", "").id == "ontrack_git___github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_2() {
        assert new GitRepository("ontrack", "git@github.com:nemerosa/ontrack.git", "", "").id == "ontrack_git_github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_3() {
        assert new GitRepository("ontrack", "https://github.com/nemerosa/ontrack.git", "", "").id == "ontrack_https___github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_4() {
        assert new GitRepository("ontrack2", "https://github.com/nemerosa/ontrack.git", "", "").id == "ontrack2_https___github_com_nemerosa_ontrack_git";
    }

}