package net.nemerosa.ontrack.extension.git.client.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultGitRepositoryManagerTest {

    @Test
    public void getRepositoryId_1() {
        assertEquals(
                "git___github_com_nemerosa_ontrack_git_master",
                new DefaultGitRepositoryManager(null).getRepositoryId("git://github.com/nemerosa/ontrack.git", "master"));
    }

    @Test
    public void getRepositoryId_2() {
        assertEquals(
                "git_github_com_nemerosa_ontrack_git_master",
                new DefaultGitRepositoryManager(null).getRepositoryId("git@github.com:nemerosa/ontrack.git", "master"));
    }

    @Test
    public void getRepositoryId_3() {
        assertEquals(
                "https___github_com_nemerosa_ontrack_git_master",
                new DefaultGitRepositoryManager(null).getRepositoryId("https://github.com/nemerosa/ontrack.git", "master"));
    }

    @Test
    public void getRepositoryId_4() {
        assertEquals(
                "git___github_com_nemerosa_ontrack_git_1_x",
                new DefaultGitRepositoryManager(null).getRepositoryId("git://github.com/nemerosa/ontrack.git", "1.x"));
    }

}
