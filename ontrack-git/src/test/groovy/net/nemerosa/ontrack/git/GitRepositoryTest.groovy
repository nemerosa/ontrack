package net.nemerosa.ontrack.git

import org.junit.Test

class GitRepositoryTest {

    @Test
    void getRepositoryId_1() {
        assert new GitRepository("basic", "ontrack", "git://github.com/nemerosa/ontrack.git", "", "").id == "basic_ontrack_git___github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_2() {
        assert new GitRepository("basic", "ontrack", "git@github.com:nemerosa/ontrack.git", "", "").id == "basic_ontrack_git_github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_3() {
        assert new GitRepository("github", "ontrack", "git@github.com:nemerosa/ontrack.git", "", "").id == "github_ontrack_git_github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_4() {
        assert new GitRepository("basic", "ontrack", "https://github.com/nemerosa/ontrack.git", "", "").id == "basic_ontrack_https___github_com_nemerosa_ontrack_git";
    }

    @Test
    void getRepositoryId_5() {
        assert new GitRepository("basic", "ontrack2", "https://github.com/nemerosa/ontrack.git", "", "").id == "basic_ontrack2_https___github_com_nemerosa_ontrack_git";
    }

}