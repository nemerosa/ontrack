package net.nemerosa.ontrack.extension.scm.model

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * List of [BranchInfo] associated with a type.
 */
class BranchInfos(
    @APIDescription("Name of the group of branches")
    val type: String,
    @APIDescription("List of information per branch")
    val branchInfoList: List<BranchInfo>
) {
    companion object {
        fun toList(map: Map<String, List<BranchInfo>>): List<BranchInfos> {
            return map.map { (type, branchInfoList) ->
                BranchInfos(type, branchInfoList)
            }
        }
    }
}