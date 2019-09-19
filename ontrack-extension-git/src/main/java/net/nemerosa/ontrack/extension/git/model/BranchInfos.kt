package net.nemerosa.ontrack.extension.git.model

/**
 * List of [BranchInfo] associated with a type.
 */
class BranchInfos(
        val type: String,
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