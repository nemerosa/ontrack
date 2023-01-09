package net.nemerosa.ontrack.git.exceptions

/**
 * Thrown when a fetch/clone operation fails because the remote does not exist.
 */
class GitRepositoryNoRemoteException(
    remote: String,
) : GitRepositoryException(
    "Remote Git repository does not exist: $remote"
)