package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.ProjectEntity

/**
 * The answer the [PreviousPromotionConditionService] cascade arrives at for one promotion level, and
 * where it came from.
 *
 * The [source] is part of the answer rather than an implementation detail of the search, because the
 * two callers need different halves of it and neither can recover the other's half: the delivery map
 * reads [required] alone, while [PreviousPromotionConditionCheckExtension] has to name the deciding
 * entity in the exception it throws. A boolean-only resolver would leave the check extension walking
 * the same four levels a second time to find out who decided.
 *
 * @property required Whether the immediate predecessor promotion is required.
 * @property source The entity carrying the property that decided, or `null` when the answer comes
 * from the global [PreviousPromotionConditionSettings]. "Nothing decided" is not a case: the settings
 * provider defaults to `false`, so the cascade always terminates.
 */
data class PreviousPromotionConditionResolution(
    val required: Boolean,
    val source: ProjectEntity?,
)
