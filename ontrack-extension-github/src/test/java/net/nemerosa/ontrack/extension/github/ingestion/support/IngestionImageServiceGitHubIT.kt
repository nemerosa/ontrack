package net.nemerosa.ontrack.extension.github.ingestion.support

import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal class IngestionImageServiceGitHubIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionImageService: IngestionImageService

    @Test
    @TestOnGitHub
    fun `Downloading an image using path only`() {
        project {
            gitHubRealConfig()
            val doc = ingestionImageService.downloadImage(
                this,
                "${githubTestEnv.organization}/${githubTestEnv.repository}/${githubTestEnv.paths.images.validation}"
            )
            assertEquals("image/png", doc.type)
            assertTrue(doc.content.isNotEmpty(), "Image is loaded")
        }
    }

    @Test
    @TestOnGitHub
    fun `Downloading an image using protocol and path`() {
        project {
            gitHubRealConfig()
            val doc = ingestionImageService.downloadImage(
                this,
                "github:${githubTestEnv.organization}/${githubTestEnv.repository}/${githubTestEnv.paths.images.validation}"
            )
            assertEquals("image/png", doc.type)
            assertTrue(doc.content.isNotEmpty(), "Image is loaded")
        }
    }

    @Test
    @TestOnGitHub
    fun `Downloading an image not found`() {
        project {
            gitHubRealConfig()
            assertFailsWith<IngestionImageNotFoundException> {
                ingestionImageService.downloadImage(
                    this,
                    "${githubTestEnv.organization}/${githubTestEnv.repository}/not-found.png"
                )
            }
        }
    }

    @Test
    fun `Downloading an image with unknown protocol`() {
        project {
            configureGitHub()
            assertFailsWith<IngestionImageProtocolUnsupportedException> {
                ingestionImageService.downloadImage(
                    this,
                    "unknown:org/repo/path/to/image.png"
                )
            }
        }
    }

    @Test
    fun `Downloading an image with incorrect format`() {
        project {
            configureGitHub()
            assertFailsWith<IngestionImageRefFormatException> {
                ingestionImageService.downloadImage(
                    this,
                    "github:repo/image.png"
                )
            }
        }
    }

    @Test
    fun `Downloading an image with incorrect image type`() {
        project {
            configureGitHub()
            assertFailsWith<IngestionImagePNGException> {
                ingestionImageService.downloadImage(
                    this,
                    "github:org/repo/path/to/image.jpg"
                )
            }
        }
    }

    @Test
    fun `Downloading an image with incorrect project configuration`() {
        project {
            assertFailsWith<IngestionImageMissingGitException> {
                ingestionImageService.downloadImage(
                    this,
                    "github:org/repo/path/to/image.png"
                )
            }
        }
    }

}