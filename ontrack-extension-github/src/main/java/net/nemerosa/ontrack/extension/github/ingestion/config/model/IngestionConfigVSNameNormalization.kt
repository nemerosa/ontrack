package net.nemerosa.ontrack.extension.github.ingestion.config.model

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationStamp

/**
 * Defines the way a computed name must be normalized before it can be used
 * as a validation stamp name.
 */
enum class IngestionConfigVSNameNormalization(
    val normalization: (name: String) -> String
) {

    /**
     * Used by default in the v1 format. Lowercase, and all illegal characters (including spaces)
     * are replaced by "-"
     */
    LEGACY({ name ->
        NameDescription.escapeName(name.lowercase()).take(ValidationStamp.NAME_MAX_LENGTH)
    }),

    /**
     * Uses by default in the v2 format. Relies on [ValidationStamp.normalizeValidationStampName].
     */
    DEFAULT({ name ->
        ValidationStamp.normalizeValidationStampName(name)
    });

    /**
     * Direct invocation of the normalization function.
     */
    operator fun invoke(name: String) = normalization(name)

}