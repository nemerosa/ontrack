package net.nemerosa.ontrack.service.ordering

import net.nemerosa.ontrack.model.ordering.BranchOrdering
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

@Component
class VersionBranchOrdering : BranchOrdering {

    override val id: String = "version"

    override fun getComparator(param: String?): Comparator<Branch> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}