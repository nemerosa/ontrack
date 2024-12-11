package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.model.files.FileRefService
import net.nemerosa.ontrack.model.files.downloadDocument
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class DefaultIngestionImageService(
    private val propertyService: PropertyService,
    private val scmRefService: FileRefService,
) : IngestionImageService {

    override fun downloadImage(project: Project, ref: String): Document {
        val parsedRef = IngestionImageRefParser.parseRef(ref)
        return when (parsedRef.protocol) {
            "github" -> downloadFromGitHub(project, parsedRef.path)
            "scm" -> {
                val uri = "scm://${parsedRef.path}"
                scmRefService.downloadDocument(uri, "image/png")
                    ?: throw IngestionImageNotFoundException(uri)
            }

            else -> throw IngestionImageProtocolUnsupportedException(parsedRef.protocol, ref)
        }
    }

    private fun downloadFromGitHub(project: Project, path: String): Document =
        if (!path.endsWith(".png")) {
            throw IngestionImagePNGException(path)
        } else {
            // GitHub configuration
            val gitHubConfig =
                propertyService.getProperty(project, GitHubProjectConfigurationPropertyType::class.java).value
                    ?.configuration
                    ?: throw IngestionImageMissingGitException(
                        path,
                        """Project $project is not configured for GitHub"""
                    )
            // Path analysis
            val tokens = path.split("/")
            if (tokens.size > 2) {
                val owner = tokens[0]
                val repository = tokens[1]
                val rest = tokens.drop(2).joinToString("/")
                downloadFromGitHub(gitHubConfig, owner, repository, rest)
            } else {
                throw IngestionImageRefFormatException(
                    path,
                    "GitHub expected format: <owner>/<repository>/<path to image>"
                )
            }
        }

    private fun downloadFromGitHub(
        gitHubConfig: GitHubEngineConfiguration,
        owner: String,
        repository: String,
        path: String,
    ): Document {
        val uri = "scm://github/${gitHubConfig.name}/$owner/$repository/$path"
        return scmRefService.downloadDocument(uri, "image/png")
            ?: throw IngestionImageNotFoundException("$owner/$repository/$path")
    }


}