package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.buildfilter.StandardFilterDataBuilder
import java.time.LocalDate

data class StandardBuildFilterData(
    val count: Int,
    val sincePromotionLevel: String? = null,
    val withPromotionLevel: String? = null,
    val afterDate: LocalDate? = null,
    val beforeDate: LocalDate? = null,
    val sinceValidationStamp: String? = null,
    val sinceValidationStampStatus: String? = null,
    val withValidationStamp: String? = null,
    val withValidationStampStatus: String? = null,
    val withProperty: String? = null,
    val withPropertyValue: String? = null,
    val sinceProperty: String? = null,
    val sincePropertyValue: String? = null,
    val linkedFrom: String? = null,
    val linkedFromPromotion: String? = null,
    val linkedTo: String? = null,
    val linkedToPromotion: String? = null
) : StandardFilterDataBuilder<StandardBuildFilterData> {

    override fun withSincePromotionLevel(sincePromotionLevel: String): StandardBuildFilterData {
        return if (this.sincePromotionLevel === sincePromotionLevel) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withWithPromotionLevel(withPromotionLevel: String): StandardBuildFilterData {
        return if (this.withPromotionLevel === withPromotionLevel) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withAfterDate(afterDate: LocalDate): StandardBuildFilterData {
        return if (this.afterDate === afterDate) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withBeforeDate(beforeDate: LocalDate): StandardBuildFilterData {
        return if (this.beforeDate === beforeDate) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withSinceValidationStamp(sinceValidationStamp: String): StandardBuildFilterData {
        return if (this.sinceValidationStamp === sinceValidationStamp) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withSinceValidationStampStatus(sinceValidationStampStatus: String): StandardBuildFilterData {
        return if (this.sinceValidationStampStatus === sinceValidationStampStatus) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withWithValidationStamp(withValidationStamp: String): StandardBuildFilterData {
        return if (this.withValidationStamp === withValidationStamp) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withWithValidationStampStatus(withValidationStampStatus: String): StandardBuildFilterData {
        return if (this.withValidationStampStatus === withValidationStampStatus) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withWithProperty(withProperty: String): StandardBuildFilterData {
        return if (this.withProperty === withProperty) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withWithPropertyValue(withPropertyValue: String): StandardBuildFilterData {
        return if (this.withPropertyValue === withPropertyValue) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withSinceProperty(sinceProperty: String): StandardBuildFilterData {
        return if (this.sinceProperty === sinceProperty) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withSincePropertyValue(sincePropertyValue: String): StandardBuildFilterData {
        return if (this.sincePropertyValue === sincePropertyValue) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withLinkedFrom(linkedFrom: String): StandardBuildFilterData {
        return if (this.linkedFrom === linkedFrom) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withLinkedFromPromotion(linkedFromPromotion: String): StandardBuildFilterData {
        return if (this.linkedFromPromotion === linkedFromPromotion) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withLinkedTo(linkedTo: String): StandardBuildFilterData {
        return if (this.linkedTo === linkedTo) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    override fun withLinkedToPromotion(linkedToPromotion: String): StandardBuildFilterData {
        return if (this.linkedToPromotion === linkedToPromotion) this else StandardBuildFilterData(
            count,
            sincePromotionLevel,
            withPromotionLevel,
            afterDate,
            beforeDate,
            sinceValidationStamp,
            sinceValidationStampStatus,
            withValidationStamp,
            withValidationStampStatus,
            withProperty,
            withPropertyValue,
            sinceProperty,
            sincePropertyValue,
            linkedFrom,
            linkedFromPromotion,
            linkedTo,
            linkedToPromotion
        )
    }

    companion object {
        @JvmStatic
        fun of(count: Int): StandardBuildFilterData {
            return StandardBuildFilterData(
                count,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }
    }
}