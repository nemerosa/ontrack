package net.nemerosa.ontrack.extension.api.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.split
import java.util.*

/**
 * Request for the export of a change log.
 *
 *
 * Additionally to the [change log request][net.nemerosa.ontrack.extension.api.model.BuildDiffRequest],
 * this request defines how to [group][.grouping] issues by type and which [format][.format]
 * to use for the export.
 *
 *
 * If no [grouping][.grouping] is specified, the issues will be returned as a list, without any grouping.
 *
 *
 * The [exclude][.exclude] field is used to exclude some issues from the export using their type.
 *
 *
 * Note that both the export format and the type of issues are specific notions linked to the issue service
 * (JIRA, GitHub...) that produces the issues.
 */
class IssueChangeLogExportRequest : BuildDiffRequest() {

    /**
     * Export format, "text" by default
     */
    var format = "text"

    /**
     * Specification for the grouping, none by default.
     *
     *
     * This specification describes how to group issues using their type. This format is independent from
     * the issue service and this service is responsible for the actual mapping of issue "types" into actual
     * issue fields. For example, for GitHub, an issue type is mapped on a label.
     *
     *
     * The format of the specification is:
     *
     *
     * <pre>
     * specification := "" | $group ( "|" $group)*
     * group         := $name "=" $type ( "," $type)*
    </pre> *
     *
     *
     * For example:
     *
     *
     * <pre>
     * Bugs=bug|Features=feature,enhancement
    </pre> *
     *
     *
     * Any issue that would not be mapped in a group will be assigned arbitrarily to the group defined by
     * [.altGroup].
     *
     *
     * If the specification is empty, no grouping will be done.
     */
    var grouping = ""

    /**
     * Comma separated list of issue types to exclude from the export.
     */
    var exclude = ""

    /**
     * Title of the group to use when an issue does not belong to any group. It defaults to "Other".
     * If left empty, any issue not belonging to a group would be excluded from the export. This field
     * is not used when no grouping is specified.
     */
    var altGroup = "Other"

    /**
     * Parses the specification and returns a map of groups x set of types. The map will be empty
     * if no group is defined, but never null.
     */
    val groupingSpecification: Map<String, Set<String>>
        @JsonIgnore
        get() {
            val result = LinkedHashMap<String, Set<String>>()
            if (!StringUtils.isBlank(this.grouping)) {
                val groups = split(this.grouping, '|')
                for (group in groups) {
                    val groupSpec = split(group.trim(), '=')
                    if (groupSpec.size != 2) throw ExportRequestGroupingFormatException(this.grouping)
                    val groupName = groupSpec[0].trim()
                    result[groupName] = split(groupSpec[1].trim(), ',').map {
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
        get() = if (StringUtils.isBlank(this.exclude)) {
            emptySet()
        } else {
            split(this.exclude, ",").map { it.trim() }.toSet()
        }
}
