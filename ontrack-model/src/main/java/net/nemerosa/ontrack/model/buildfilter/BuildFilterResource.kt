package net.nemerosa.ontrack.model.buildfilter

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.structure.Branch

/**
 * Custom-defined build filter.
 *
 * @param <T> Type of configuration data for this build filter
 * @property branch Branch
 * @property isShared Shared filter?
 * @property name Name for this filter
 * @property type Type for this filter
 * @property data Specific data for this filter
 * @property error Error message if this filter is not valid
 */
class BuildFilterResource<T>(
        @JsonIgnore
        val branch: Branch,
        val isShared: Boolean,
        val name: String,
        val type: String,
        val data: T,
        val error: String?
)
