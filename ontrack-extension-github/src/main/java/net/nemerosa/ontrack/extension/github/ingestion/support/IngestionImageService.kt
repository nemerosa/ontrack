package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.structure.Project

/**
 * Service used to download images using `image` references.
 */
interface IngestionImageService {

    fun downloadImage(project: Project, ref: String): Document

}