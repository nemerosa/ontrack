package net.nemerosa.ontrack.service

data class NamedBuildFilterData(
    val count: Int = 10,
    val fromBuild: String,
    val toBuild: String? = null,
    val withPromotionLevel: String? = null,
) {

    fun withCount(count: Int): NamedBuildFilterData {
        return if (this.count == count) this else NamedBuildFilterData(count, fromBuild, toBuild, withPromotionLevel)
    }

    fun withToBuild(toBuild: String): NamedBuildFilterData {
        return if (this.toBuild === toBuild) this else NamedBuildFilterData(
            count,
            fromBuild,
            toBuild,
            withPromotionLevel
        )
    }

    fun withWithPromotionLevel(withPromotionLevel: String): NamedBuildFilterData {
        return if (this.withPromotionLevel === withPromotionLevel) this else NamedBuildFilterData(
            count,
            fromBuild,
            toBuild,
            withPromotionLevel
        )
    }

    companion object {
        fun of(fromBuild: String): NamedBuildFilterData {
            return NamedBuildFilterData(10, fromBuild, null, null)
        }
    }
}