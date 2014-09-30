package net.nemerosa.ontrack.extension.git.client.impl;

import net.nemerosa.ontrack.model.support.UserPassword;

import java.util.Optional;
import java.util.function.Supplier;

public interface GitRepositoryManager {

    GitRepository getRepository(String remote, String branch, Supplier<Optional<UserPassword>> userPasswordSupplier);

}
