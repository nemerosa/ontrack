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

// TODO Text rendering of the complete change log, using specified format

@RestController
@API("Release notes")
@RequestMapping("/extension/scm/release-notes")
class ReleaseNotesController(
        private val structureService: StructureService,
        private val releaseNotesService: ReleaseNotesService
) : AbstractResourceController() {

    // TODO Async collection of release notes
    @APIDescription("Structured release notes for a project")
    @GetMapping("project/{projectId}")
    fun getProjectReleaseNotes(@PathVariable projectId: ID, form: ReleaseNotesForm): Resource<ReleaseNotes> {
        // Converts to a request
        val request = toRequest(form)
        // Loads the project
        val project = structureService.getProject(projectId)
        // Gets the release notes
        val releaseNotes = releaseNotesService.getProjectReleaseNotes(project, request)
        // Returns the resource
        return Resource.of(
                releaseNotes,
                uri(on(this::class.java).getProjectReleaseNotes(projectId, form))
        )
    }

    // TODO Async collection of release notes
    @APIDescription("Text release notes for a project")
    @GetMapping("project/{projectId}/export")
    fun exportProjectReleaseNotes(@PathVariable projectId: ID, form: ReleaseNotesForm): HttpEntity<String> {
        // Converts to a request
        val request = toRequest(form)
        // Loads the project
        val project = structureService.getProject(projectId)
        // Gets the release notes
        val releaseNotes: Document = releaseNotesService.exportProjectReleaseNotes(project, request)
        // Returns the resource
        return ResponseEntity.ok().contentType(MediaType(releaseNotes.type)).body(releaseNotes.content.toString())

    }

    private fun toRequest(form: ReleaseNotesForm): ReleaseNotesRequest = form.run {
        ReleaseNotesRequest(
                branchPattern = branchPattern,
                branchGrouping = branchGrouping,
                branchOrdering = branchOrdering ?: throw ReleaseNotesBranchOrderingMissingException(),
                buildLimit = buildLimit,
                promotion = promotion ?: throw ReleaseNotesPromotionMissingException(),
                issueGrouping = issueGrouping,
                issueExclude = issueExclude,
                issueAltGroup = issueAltGroup,
                format = format
        )
    }

}