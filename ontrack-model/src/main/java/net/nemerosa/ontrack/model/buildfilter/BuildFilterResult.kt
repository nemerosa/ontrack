package net.nemerosa.ontrack.model.buildfilter


data class BuildFilterResult private constructor(
    val accept: Boolean = false,
    val goingOn: Boolean = false,
) {

    fun goingOn(): BuildFilterResult {
        return BuildFilterResult(accept, true)
    }

    fun stop(): BuildFilterResult {
        return BuildFilterResult(accept, false)
    }

    fun acceptIf(condition: Boolean): BuildFilterResult {
        return BuildFilterResult(accept && condition, goingOn)
    }

    fun goOnIf(condition: Boolean): BuildFilterResult {
        return BuildFilterResult(accept, goingOn && condition)
    }

    fun doAccept(): BuildFilterResult {
        return BuildFilterResult(true, goingOn)
    }

    companion object {
        fun stopNowIf(condition: Boolean): BuildFilterResult {
            return BuildFilterResult(!condition, !condition)
        }

        fun stopNow(): BuildFilterResult {
            return BuildFilterResult(false, false)
        }

        fun ok(): BuildFilterResult {
            return BuildFilterResult(true, true)
        }

        fun notAccept(): BuildFilterResult {
            return BuildFilterResult(false, true)
        }

        fun accept(): BuildFilterResult {
            return BuildFilterResult(true, true)
        }
    }
}
