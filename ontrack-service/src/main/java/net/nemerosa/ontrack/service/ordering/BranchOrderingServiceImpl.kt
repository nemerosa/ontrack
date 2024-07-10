package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import net.nemerosa.ontrack.model.ordering.VersionUtils
import net.nemerosa.ontrack.model.ordering.VersionUtils.semVerSuffixRegex
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.BranchDisplayNameService
import net.nemerosa.ontrack.model.structure.BranchNamePolicy
import org.springframework.stereotype.Service

@Service
class BranchOrderingServiceImpl(
    orderings: List<BranchOrdering>,
    private val branchDisplayNameService: BranchDisplayNameService,
) : BranchOrderingService {

    private val index = orderings.associateBy { it.id }

    @Deprecated("Will be removed in V5. Consider using built-in orderings.")
    override fun getBranchOrdering(id: String): BranchOrdering? = index[id]

    @Deprecated("Will be removed in V5. Consider using built-in orderings.")
    override val branchOrderings: List<BranchOrdering> = index.values.sortedBy { it.id }

    override fun getSemVerBranchOrdering(branchNamePolicy: BranchNamePolicy): Comparator<Branch> {
        return compareByDescending { branch ->
            val name = branchDisplayNameService.getBranchDisplayName(branch, branchNamePolicy)
            VersionUtils.getVersion(semVerSuffixRegex, name)
        }
    }
}