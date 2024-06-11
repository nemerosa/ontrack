package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.it.AbstractITTestSupport
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractRepositoryTestSupport : AbstractITTestSupport() {

    @Autowired
    protected lateinit var structureRepository: StructureRepository

    protected fun do_create_project(): Project {
        return structureRepository.newProject(of(nameDescription()))
    }

    protected fun do_create_branch(): Branch {
        return structureRepository.newBranch(of(do_create_project(), nameDescription()))
    }
}
