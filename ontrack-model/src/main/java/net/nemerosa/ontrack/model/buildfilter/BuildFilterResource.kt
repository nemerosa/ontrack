package net.nemerosa.ontrack.model.buildfilter

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.annotations.APIDescription
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
data class BuildFilterResource<T>(
        @JsonIgnore
        val branch: Branch,
        @APIDescription("Is this filter shared?")
        val isShared: Boolean,
        @APIDescription("Name for this filter")
        val name: String,
        @APIDescription("FQCN of the build filter provider")
        val type: String,
        @APIDescription("Data for this filter")
        val data: T,
        @APIDescription("Filter error if any")
        val error: String?,
)
