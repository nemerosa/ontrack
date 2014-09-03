package net.nemerosa.ontrack.extension.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request for the export of a change log.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class IssueChangeLogExportRequest extends BuildDiffRequest {

    /**
     * Export format, "text" by default
     */
    private String format = "text";

    /**
     * Specification for the grouping, none by default.
     * <p>
     * This specification describes how to group issues using their type. This format is independent from
     * the issue service and this service is responsible for the actual mapping of issue "types" into actual
     * issue fields. For example, for GitHub, an issue type is mapped on a label.
     * <p>
     * The format of the specification is:
     * <p>
     * <pre>
     *     specification := "" | $group ( "|" $group)*
     *     group         := $name "=" $type ( "," $type)*
     * </pre>
     * <p>
     * For example:
     * <p>
     * <pre>
     *     Bugs=bug|Features=feature,enhancement
     * </pre>
     * <p>
     * Any issue that would not be mapped in a group will be assigned arbitrarily to the "Other" group.
     * <p>
     * If the specification is empty, no grouping will be done.
     */
    private String grouping = "";

}
