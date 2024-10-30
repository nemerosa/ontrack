package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.model.annotations.getPropertyLabel
import net.nemerosa.ontrack.model.annotations.getPropertyName
import kotlin.reflect.KProperty

fun inputText(property: KProperty<String?>, value: String?) = SlotAdmissionRuleInputField(
    type = SlotAdmissionRuleInputFieldType.TEXT,
    name = getPropertyName(property),
    label = getPropertyLabel(property),
    value = value,
)

fun inputBoolean(property: KProperty<Boolean?>, value: Boolean?) = SlotAdmissionRuleInputField(
    type = SlotAdmissionRuleInputFieldType.BOOLEAN,
    name = getPropertyName(property),
    label = getPropertyLabel(property),
    value = value,
)