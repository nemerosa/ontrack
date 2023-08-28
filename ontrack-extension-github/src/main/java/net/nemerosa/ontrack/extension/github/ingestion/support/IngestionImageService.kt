package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.structure.Project

/**
 * Service used to download images using `image` references.
 */
interface IngestionImageService {

    /**
     * To use the new SCMRefService we need a reference like:
     *
     * * if protocol is `github`, we just use the following ref:
     *
     * `scm://github/<config>/<path>` where config is the name of the project configuration - it must be GitHub
     *
     * * if the protocol is scm, we just use
     *
     * `scm://<path>`
     *
     * * if there is no protocol, we throw an error
     */
    fun downloadImage(project: Project, ref: String): Document

}