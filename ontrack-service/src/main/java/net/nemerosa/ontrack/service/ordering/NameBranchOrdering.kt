package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

/**
 * [Branch ordering][BranchOrdering] based on the [name][Branch.name] of the branch.
 */
@Component
class NameBranchOrdering : BranchOrdering {

    override val id: String = "name"

    override fun getComparator(param: String?): Comparator<Branch> =
            compareByDescending { it.name }
}