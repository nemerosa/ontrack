package net.nemerosa.ontrack.extension.av.config

import org.springframework.stereotype.Component

@Component
class BranchSourceFactoryImpl(
    branchSources: List<BranchSource>
) : BranchSourceFactory {

    private val index = branchSources.associateBy { it.id }

    override fun getBranchSource(id: String): BranchSource =
        index[id] ?: throw BranchSourceIdNotFoundException(id)
}