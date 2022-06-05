package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.structure.Branch

/**
 * High-level interaction with the remote repository of a project.
 *
 * Any instance is specific to the project it was created for.
 */
interface SCM {

    /**
     * Given a branch, returns its SCM branch if any.
     *
     * Throws an error if the branch does not belong to the SCM's project.
     */
    fun getSCMBranch(branch: Branch): String?

    /**
     * Creates a new branch in a project.
     *
     * @param sourceBranch Source branch
     * @param newBranch New branch
     * @return Commit of the new branch
     */
    fun createBranch(sourceBranch: String, newBranch: String): String

    /**
     * Downloads the content of a file.
     *
     * @param scmBranch Branch to download the file from
     * @param path Path to the file
     * @return Binary content of the file or null if non existing
     */
    fun download(scmBranch: String, path: String): ByteArray?

}