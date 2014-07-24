package net.nemerosa.ontrack.extension.git.client.impl;

public interface GitRepositoryManager {

    GitRepository getRepository(String remote, String branch);

}
