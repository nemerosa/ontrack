package net.nemerosa.ontrack.demo.seed

import java.util.regex.Pattern

/**
 * The dataset's own reading of the two promotion properties a promotion level can carry.
 *
 * Repeated from the server rather than depended on, for the reason the rest of this module gives: it
 * talks to Yontrack over its API, not over its model classes. Shared between [validate] and
 * `InMemoryDemoTarget` rather than written twice, for the reason `SlotAdmissionRuleReadings` gives:
 * two copies of one rule let the check that guards a reset drift from the fake server the unit tests
 * run the seed against, and the tests would then pass on a dataset the reset refuses.
 */

/**
 * Whether an auto promotion pattern selects a validation stamp by name.
 *
 * `AutoPromotionProperty.contains` on the server side, whose patterns are whole-string regular
 * expressions - `Pattern.matches`, not `find` - and whose blank pattern matches nothing at all
 * rather than everything.
 */
internal fun autoPromotionSelectsStamp(name: String, spec: AutoPromotionSpec): Boolean {
    fun matches(pattern: String) = pattern.isNotBlank() && Pattern.matches(pattern, name)
    return name in spec.validationStamps || (matches(spec.include) && !matches(spec.exclude))
}

/**
 * The first promotion a build is missing out of the ones its promotion depends on, or null when it
 * is missing none.
 *
 * `PromotionRunDependenciesCheckExtension` on the server side, which refuses the promotion outright.
 * The order matters as much as the set: the check runs as the promotion is created, so a build
 * promoted to GOLD before SILVER is refused even though it ends up carrying both.
 *
 * @param alreadyPromoted The promotions the build carries at that moment, in order
 * @param dependencies What the promotion being granted depends on
 */
internal fun missingPromotionDependency(alreadyPromoted: List<String>, dependencies: List<String>): String? =
    dependencies.firstOrNull { it !in alreadyPromoted }
