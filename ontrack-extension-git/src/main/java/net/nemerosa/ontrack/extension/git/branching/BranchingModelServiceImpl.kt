package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchingModelServiceImpl : BranchingModelService {
    /**
     * No customization available yet.
     */
    override fun getBranchingModel(project: Project): BranchingModel {
        return BranchingModel.DEFAULT
    }
}