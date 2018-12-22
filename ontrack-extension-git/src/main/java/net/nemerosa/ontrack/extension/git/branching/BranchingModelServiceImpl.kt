package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BranchingModelServiceImpl(
        private val propertyService: PropertyService
) : BranchingModelService {
    override fun getBranchingModel(project: Project): BranchingModel {
        return propertyService
                .getProperty(project, BranchingModelPropertyType::class.java)
                .value
                ?.let { BranchingModel(it.patterns) }
                ?: BranchingModel.DEFAULT
    }
}