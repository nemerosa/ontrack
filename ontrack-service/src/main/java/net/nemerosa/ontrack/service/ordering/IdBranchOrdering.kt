package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

/**
 * Comparator ordering the branches from the most recent ID to the oldest one.
 */
@Component
class IdBranchOrdering : BranchOrdering {

    override val id: String = "id"

    override fun getComparator(param: String?): Comparator<Branch> =
            compareByDescending { it.id() }
}