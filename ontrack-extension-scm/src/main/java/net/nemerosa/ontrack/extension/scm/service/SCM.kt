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

    /**
     * Uploads the content of a file to a branch.
     *
     * @param scmBranch Branch to upload the file to
     * @param commit Commit of the branch
     * @param path Path to the file
     * @param content Binary content of the file
     */
    fun upload(scmBranch: String, commit: String, path: String, content: ByteArray)

    /**
     * Creates a pull request from the branch [from] to the branch [to], with a given [title] and
     * [description]. If [autoApproval] is `true`, the pull request will have a reviewer approving
     * it immediately.
     *
     * @param from Origin branch
     * @param to Target branch
     * @param title Title for the pull request
     * @param description Description for the pull request
     * @param autoApproval Must the created pull request be auto-approved?
     * @param remoteAutoMerge If the SCM allows this, sets the PR in auto merge mode
     * @return PR information
     */
    fun createPR(
        from: String,
        to: String,
        title: String,
        description: String,
        autoApproval: Boolean,
        remoteAutoMerge: Boolean,
    ): SCMPullRequest

}