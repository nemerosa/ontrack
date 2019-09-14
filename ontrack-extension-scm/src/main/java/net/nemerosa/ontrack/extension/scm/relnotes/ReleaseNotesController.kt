package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.annotations.API
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.concurrent.Callable

@RestController
@API("Release notes")
@RequestMapping("/extension/scm/release-notes")
class ReleaseNotesController(
        private val structureService: StructureService,
        private val releaseNotesService: ReleaseNotesService
) : AbstractResourceController() {

    @APIDescription("Structured release notes for a project")
    @GetMapping("project/{projectId}")
    fun getProjectReleaseNotes(@PathVariable projectId: ID, form: ReleaseNotesForm): Callable<Resource<ReleaseNotes>> {
        return Callable {
            // Converts to a request
            val request = toRequest(form)
            // Loads the project
            val project = structureService.getProject(projectId)
            // Gets the release notes
            val releaseNotes = releaseNotesService.getProjectReleaseNotes(project, request)
            // Returns the resource
            Resource.of(
                    releaseNotes,
                    uri(on(this::class.java).getProjectReleaseNotes(projectId, form))
            )
        }
    }

    @APIDescription("Text release notes for a project")
    @GetMapping("project/{projectId}/export")
    fun exportProjectReleaseNotes(@PathVariable projectId: ID, form: ReleaseNotesForm): Callable<HttpEntity<String>> {
        return Callable {
            // Converts to a request
            val request = toRequest(form)
            // Loads the project
            val project = structureService.getProject(projectId)
            // Gets the release notes
            val releaseNotes: Document = releaseNotesService.exportProjectReleaseNotes(project, request)
            // Returns the resource
            ResponseEntity.ok().contentType(MediaType(releaseNotes.type)).body(releaseNotes.content.toString())
        }
    }

    private fun toRequest(form: ReleaseNotesForm): ReleaseNotesRequest = form.run {
        ReleaseNotesRequest(
                branchPattern = branchPattern,
                branchGrouping = branchGrouping,
                branchGroupFormat = branchGroupFormat,
                branchOrdering = branchOrdering,
                branchOrderingParameter = branchOrderingParameter,
                buildLimit = buildLimit,
                promotion = promotion ?: throw ReleaseNotesPromotionMissingException(),
                issueGrouping = issueGrouping,
                issueExclude = issueExclude,
                issueAltGroup = issueAltGroup,
                format = format
        )
    }

}