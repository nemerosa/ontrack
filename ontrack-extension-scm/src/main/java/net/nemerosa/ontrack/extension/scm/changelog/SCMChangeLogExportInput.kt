package net.nemerosa.ontrack.extension.scm.changelog

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.api.model.ExportRequestGroupingFormatException
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.apache.commons.lang3.StringUtils
import java.util.LinkedHashMap

data class SCMChangeLogExportInput(
    val format: String? = PlainEventRenderer.INSTANCE.id,
    val grouping: String? = "",
    val exclude: String? = "",
    val altGroup: String? = "",
) {

    /**
     * Parses the specification and returns a map of groups x set of types. The map will be empty
     * if no group is defined, but never null.
     */
    val groupingSpecification: Map<String, Set<String>>
        @JsonIgnore
        get() {
            val result = mutableMapOf<String, Set<String>>()
            if (!grouping.isNullOrBlank()) {
                val groups = grouping.split('|')
                for (group in groups) {
                    val groupSpec = group.trim().split('=')
                    if (groupSpec.size != 2) throw ExportRequestGroupingFormatException(grouping)
                    val groupName = groupSpec[0].trim()
                    result[groupName] = groupSpec[1].trim().split(',').map {
                        it.trim()
                    }.toSet()
                }
            }
            return result
        }

    /**
     * Parses the comma-separated list of excluded types.
     */
    val excludedTypes: Set<String>
        @JsonIgnore
        get() = if (exclude.isNullOrBlank()) {
            emptySet()
        } else {
            exclude.split(",").map { it.trim() }.toSet()
        }

}