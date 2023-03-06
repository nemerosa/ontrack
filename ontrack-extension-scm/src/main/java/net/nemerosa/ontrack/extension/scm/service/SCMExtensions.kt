package net.nemerosa.ontrack.extension.scm.service

/**
 * Uploads the content of a file to a branch.
 *
 * @param scmBranch Branch to upload the file to
 * @param commit Commit of the branch
 * @param path Path to the file
 * @param content Content as a list of lines
 * @param message Commit message
 */
fun SCM.uploadLines(scmBranch: String, commit: String, path: String, content: List<String>, message: String) {
    upload(
        scmBranch, commit, path,
        content.joinToString("\n").toByteArray(),
        message
    )
}
