package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

/**
 * High-level interaction with the remote repository of a project.
 */
interface SCM {

    /**
     * Given a branch, returns its SCM branch if any.
     */
    fun getSCMBranch(branch: Branch): String?

    /**
     * Creates a new branch in a project.
     *
     * @param project Project holding the configuration
     * @param sourceBranch Source branch
     * @param newBranch New branch
     * @return Commit of the new branch
     */
    fun createBranch(project: Project, sourceBranch: String, newBranch: String): String

    /**
     * Downloads the content of a file.
     *
     * @param project Project
     * @param scmBranch Branch to download the file from
     * @param path Path to the file
     * @return Binary content of the file or null if non existing
     */
    fun download(project: Project, scmBranch: String, targetPath: String): ByteArray?

}