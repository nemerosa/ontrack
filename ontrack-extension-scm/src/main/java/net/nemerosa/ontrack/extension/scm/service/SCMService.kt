package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.model.SCMPathInfo
import net.nemerosa.ontrack.model.structure.Branch

import java.util.Optional

/**
 * Common methods for the SCM accesses
 */
interface SCMService {

    /**
     * Downloads the file at the given path for a branch
     */
    fun download(branch: Branch, path: String): Optional<String>

    /**
     * Gets the SCM path info of a branch
     */
    fun getSCMPathInfo(branch: Branch): Optional<SCMPathInfo>

}
