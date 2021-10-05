package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.git.GitRepository

/**
 * Gets the Git repository for this configuration
 */
@Deprecated("One cannot use user/password in all cases. This method will be deleted in the context of issue #881.")
val GitConfiguration.gitRepository: GitRepository
    get() {
        TODO("#881 Authenticate using the Git configuration (we cannot assume username / password")
//        Optional<UserPassword> credentials = getCredentials();
//        return new GitRepository(
//                getType(),
//                getName(),
//                getRemote(),
//                credentials.map(UserPassword::getUser).orElse(""),
//                credentials.map(UserPassword::getPassword).orElse("")
//        );
    }
