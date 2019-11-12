package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import org.springframework.stereotype.Service

@Service
class BranchOrderingServiceImpl(
        orderings: List<BranchOrdering>
) : BranchOrderingService {

    private val index = orderings.associateBy { it.id }

    override fun getBranchOrdering(id: String): BranchOrdering? = index[id]

    override val branchOrderings: List<BranchOrdering> = index.values.sortedBy { it.id }


}