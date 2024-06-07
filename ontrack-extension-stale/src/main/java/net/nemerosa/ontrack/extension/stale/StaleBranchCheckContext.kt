package net.nemerosa.ontrack.extension.stale

interface StaleBranchCheckContext {

    companion object {
        const val ALL_BRANCHES = "branches"
    }

    fun <T : Any> getContext(name: String, valueFn: () -> T): T
    fun <T : Any> getContext(name: String): T?

}