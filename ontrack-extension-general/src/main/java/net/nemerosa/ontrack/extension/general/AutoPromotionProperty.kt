package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp
import java.util.regex.Pattern

data class AutoPromotionProperty(
        /**
         * List of needed validation stamps
         */
        val validationStamps: List<ValidationStamp>,
        /**
         * Regular expression to include validation stamps by name
         */
        val include: String,
        /**
         * Regular expression to exclude validation stamps by name
         */
        val exclude: String,
        /**
         * List of needed promotion levels
         */
        val promotionLevels: List<PromotionLevel>
) {

    /**
     * Checks if this property is empty of not
     */
    @JsonIgnore
    fun isEmpty(): Boolean {
        return validationStamps.isEmpty() &&
                promotionLevels.isEmpty() &&
                include.isBlank()
    }

    operator fun contains(vs: ValidationStamp): Boolean {
        return (containsDirectValidationStamp(vs)
                || containsByPattern(vs))
    }

    operator fun contains(pl: PromotionLevel): Boolean {
        return promotionLevels.any { pl.id() == it.id() }
    }

    fun containsDirectValidationStamp(vs: ValidationStamp): Boolean {
        return validationStamps.any { vs.id() == it.id() }
    }

    private fun containsByPattern(vs: ValidationStamp): Boolean {
        return includes(vs) && !excludes(vs)
    }

    private fun includes(vs: ValidationStamp): Boolean {
        return matches(vs, include)
    }

    private fun excludes(vs: ValidationStamp): Boolean {
        return matches(vs, exclude)
    }

    private fun matches(vs: ValidationStamp, pattern: String): Boolean {
        return pattern.isNotBlank() && Pattern.matches(pattern, vs.name)
    }

}