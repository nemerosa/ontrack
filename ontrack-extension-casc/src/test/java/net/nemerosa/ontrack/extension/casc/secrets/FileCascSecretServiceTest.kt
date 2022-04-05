package net.nemerosa.ontrack.extension.casc.secrets

import net.nemerosa.ontrack.extension.casc.CascConfigurationProperties
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class FileCascSecretServiceTest {

    @Test
    fun `Directory not defined`() {
        assertFailsWith<FileCascSecretServiceNoDirectoryException> {
            FileCascSecretService(
                CascConfigurationProperties().apply {
                    secrets.directory = ""
                }
            )
        }
    }

    @Test
    fun `Directory not existing`() {
        assertFailsWith<FileCascSecretServiceInvalidDirectoryException> {
            FileCascSecretService(
                CascConfigurationProperties().apply {
                    secrets.directory = "/a/not/existing/path"
                }
            )
        }
    }

    @Test
    fun `Ref has not value`() {
        withFileCascSecretService { service, _ ->
            assertFailsWith<FileCascSecretServiceSecretFormatException> {
                service.getValue("")
            }
        }
    }

    @Test
    fun `Ref has not base`() {
        withFileCascSecretService { service, _ ->
            assertFailsWith<FileCascSecretServiceSecretFormatException> {
                service.getValue(".name")
            }
        }
    }

    @Test
    fun `Ref has not name`() {
        withFileCascSecretService { service, _ ->
            assertFailsWith<FileCascSecretServiceSecretFormatException> {
                service.getValue("ref.")
            }
        }
    }

    @Test
    fun `Missing secret directory`() {
        withFileCascSecretService { service, _ ->
            assertFailsWith<FileCascSecretServiceSecretNotFoundException> {
                service.getValue("my-jenkins.password")
            }
        }
    }

    @Test
    fun `Missing secret value file`() {
        withFileCascSecretService { service, dir ->
            dir.resolve("my-jenkins").createDirectories()
            assertFailsWith<FileCascSecretServiceSecretNotFoundException> {
                service.getValue("my-jenkins.password")
            }
        }
    }

    @Test
    fun `Correct reference`() {
        withFileCascSecretService { service, dir ->
            dir
                .resolve("my-jenkins")
                .createDirectories()
                .resolve("password")
                .writeText("my-password")
            assertEquals(
                "my-password",
                service.getValue("my-jenkins.password")
            )
        }
    }

    private fun withFileCascSecretService(
        code: (service: FileCascSecretService, dir: Path) -> Unit,
    ) {
        val dir = createTempDirectory()
        val service = FileCascSecretService(
            CascConfigurationProperties().apply {
                secrets.directory = dir.pathString
            }
        )
        code(service, dir)
    }

}