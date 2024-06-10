package net.nemerosa.ontrack.extension.stale

class DefaultStaleBranchCheckContext : StaleBranchCheckContext {

    private val index = mutableMapOf<String, Any>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getContext(name: String): T? = index[name] as T?

    override fun <T : Any> getContext(name: String, valueFn: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        val value = index[name] as? T?
        return if (value != null) {
            value
        } else {
            val newValue = valueFn()
            index[name] = newValue
            newValue
        }
    }

}