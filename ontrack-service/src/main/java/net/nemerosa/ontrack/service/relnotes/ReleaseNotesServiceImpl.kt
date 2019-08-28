package net.nemerosa.ontrack.service.relnotes

import net.nemerosa.ontrack.model.relnotes.ReleaseNotes
import net.nemerosa.ontrack.model.relnotes.ReleaseNotesRequest
import net.nemerosa.ontrack.model.relnotes.ReleaseNotesService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReleaseNotesServiceImpl : ReleaseNotesService {

    override fun getProjectReleaseNotes(project: Project, request: ReleaseNotesRequest): ReleaseNotes {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}