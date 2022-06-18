package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.model.SCMPathInfo
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import java.util.*

/**
 * Common methods for the SCM accesses.
 *
 * Whenever possible, prefer using the [SCM] interface. The methods of [SCMService] might become
 * deprecated in preparation of Ontrack V5 and the extraction of local working copies to another service.
 */
@Deprecated("Prefer using SCM")
interface SCMService {
    /**
     * Downloads the file at the given path for a branch
     *
     */
    @Deprecated("Use the version with the branch name")
    fun download(branch: Branch, path: String): Optional<String>

    /**
     * Downloads the file at the given path for a branch
     *
     * @param project Project holding the SCM configuration
     * @param scmBranch Name of the SCM branch
     * @param path Path to the file, relative to the repository
     * @return Content of the file or null if not found
     */
    fun download(project: Project, scmBranch: String, path: String): String?

    /**
     * Gets the SCM path info of a branch
     */
    @Deprecated("Use getBranchSCMPathInfo")
    fun getSCMPathInfo(branch: Branch): Optional<SCMPathInfo>

    /**
     * Gets the SCM path info of a branch
     */
    fun getBranchSCMPathInfo(branch: Branch): SCMPathInfo?

    /**
     * Gets the name of the default SCM branch for the project
     *
     * @param project Project to get information about
     * @return Name of the default SCM branch or null if not available
     */
    fun getSCMDefaultBranch(project: Project): String?
}