package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.annotations.API
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@API("Release notes")
@RequestMapping("/extension/scm/release-notes")
class ReleaseNotesController(
        private val structureService: StructureService,
        private val releaseNotesService: ReleaseNotesService
) : AbstractResourceController() {

    // TODO Async collection of release notes
    @APIDescription("Release notes for a project")
    @GetMapping("project/{projectId}")
    fun getProjectReleaseNotes(@PathVariable projectId: ID, form: ReleaseNotesForm): Resource<ReleaseNotes> {
        // Converts to a request
        val request = form.run {
            ReleaseNotesRequest(
                    branchPattern = branchPattern,
                    branchGrouping = branchGrouping,
                    branchOrdering = branchOrdering ?: throw ReleaseNotesBranchOrderingMissingException(),
                    buildLimit = buildLimit,
                    promotion = promotion ?: throw ReleaseNotesPromotionMissingException(),
                    // TODO Configuration in form
                    issueGrouping = "",
                    issueExclude = "",
                    issueAltGroup = "Other"
            )
        }
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

}