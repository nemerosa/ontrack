package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode

enum class SlotAdmissionRuleInputFieldType {
    /**
     * Text field
     */
    TEXT {
        override fun validate(data: JsonNode?): SlotAdmissionRuleInputFieldTypeValidation {
            val text = data?.asText()
            return SlotAdmissionRuleInputFieldTypeValidation.check(
                check = !text.isNullOrBlank()
            )
        }
    },

    /**
     * Boolean field
     */
    BOOLEAN {
        override fun validate(data: JsonNode?): SlotAdmissionRuleInputFieldTypeValidation {
            val ok = data?.asBoolean() ?: false
            return SlotAdmissionRuleInputFieldTypeValidation.check(
                check = ok
            )
        }
    };

    abstract fun validate(data: JsonNode?): SlotAdmissionRuleInputFieldTypeValidation
}