package net.nemerosa.ontrack.model.buildfilter

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.structure.Branch

/**
 * Custom-defined build filter.
 *
 * @param <T> Type of configuration data for this build filter
 * @property branch Branch
 * @property shared Shared filter?
 * @property name Name for this filter
 * @property type Type for this filter
 * @property data Specific data for this filter
 */
class BuildFilterResource<T>(
        @JsonIgnore
        val branch: Branch,
        val shared: Boolean,
        val name: String,
        val type: String,
        val data: T
)
