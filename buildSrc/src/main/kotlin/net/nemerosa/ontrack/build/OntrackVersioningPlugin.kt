package net.nemerosa.ontrack.build

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@Suppress("unused")
class OntrackVersioningPlugin : Plugin<Project> {

    interface InjectedExecOps {
        @get:Inject
        val execOps: ExecOperations
    }

    override fun apply(project: Project) {
        val computed = computeVersion(project)
        project.version = computed
        project.logger.lifecycle("Computed version: $computed")

        project.tasks.register("writeVersion") {
            description = "Called by the CI engine to write the version into a file"
            doLast {
                val versionFile = project.file("build/version.txt")
                versionFile.parentFile.mkdirs()
                versionFile.writeText(project.version.toString())
                project.logger.lifecycle("Version written to: ${versionFile.absolutePath}")
            }
        }
    }

    private fun execGit(project: Project, vararg command: String): String {
        val injected = project.objects.newInstance<InjectedExecOps>()
        val output = ByteArrayOutputStream()
        val result = injected.execOps.exec {
            commandLine(*command)
            standardOutput = output
            isIgnoreExitValue = true
        }
        return if (result.exitValue == 0) output.toString().trim() else ""
    }

    private fun getCurrentBranch(project: Project): String =
        execGit(project, "git", "rev-parse", "--abbrev-ref", "HEAD").trim()

    private fun findLatestPatchVersion(project: Project, baseVersion: String, qualifier: String = ""): Int {
        val tags = execGit(project, "git", "tag", "-l").trim()
        if (tags.isEmpty()) return -1

        val pattern = if (qualifier.isNotEmpty()) {
            // Match tags like "5.0-alpha.1", "5.0-beta.2"
            Regex("^${Regex.escape(baseVersion)}-${Regex.escape(qualifier)}\\.(\\d+)$")
        } else {
            // Match tags like "5.0.1", "5.0.2"
            Regex("^${Regex.escape(baseVersion)}\\.(\\d+)$")
        }

        val matchingPatches = tags.split("\n")
            .mapNotNull { tag ->
                pattern.matchEntire(tag.trim())?.groupValues?.get(1)?.toIntOrNull()
            }

        return matchingPatches.maxOrNull() ?: -1
    }

    private fun computeMainVersion(project: Project): String {
        val versionFile = project.file("VERSION")
        if (!versionFile.exists()) {
            throw GradleException("VERSION file not found")
        }

        val versionContent = versionFile.readText().trim()
        val versionPattern = Regex("^(\\d+\\.\\d+)(?:-(alpha|beta))?$")
        val matchResult = versionPattern.matchEntire(versionContent)
            ?: throw GradleException("VERSION file should contain version in format X.Y, X.Y-alpha, or X.Y-beta (e.g., 5.0, 5.0-alpha, 5.0-beta)")

        val baseVersion = matchResult.groupValues[1]
        val qualifier = matchResult.groupValues[2]

        val latestPatch = findLatestPatchVersion(project, baseVersion, qualifier)
        val newPatch = latestPatch + 1

        return if (qualifier.isNotEmpty()) {
            "$baseVersion-$qualifier.$newPatch"
        } else {
            "$baseVersion.$newPatch"
        }
    }

    private fun computeReleaseVersion(project: Project, branchName: String): String {
        val versionFromBranch = branchName.removePrefix("release/")
        if (!versionFromBranch.matches(Regex("\\d+\\.\\d+"))) {
            throw GradleException("Release branch should be in format release/X.Y (e.g., release/5.0)")
        }
        val latestPatch = findLatestPatchVersion(project, versionFromBranch)
        val newPatch = latestPatch + 1
        return "$versionFromBranch.$newPatch"
    }

    private fun computeFeatureVersion(project: Project, branchName: String): String {
        val versionFile = project.file("VERSION")
        if (!versionFile.exists()) {
            throw GradleException("VERSION file not found")
        }
        val targetVersion = versionFile.readText().trim()
        val commitHash = execGit(project, "git", "rev-parse", "--short", "HEAD").trim()
        val sanitizedBranch = branchName.replace(Regex("[^a-zA-Z0-9._-]"), "-")
        return "$targetVersion-$sanitizedBranch-$commitHash"
    }

    private fun computeVersion(project: Project): String {
        val currentBranch = getCurrentBranch(project)
        return when {
            currentBranch == "main" -> computeMainVersion(project)
            currentBranch.startsWith("release/") -> computeReleaseVersion(project, currentBranch)
            else -> computeFeatureVersion(project, currentBranch)
        }
    }
}
