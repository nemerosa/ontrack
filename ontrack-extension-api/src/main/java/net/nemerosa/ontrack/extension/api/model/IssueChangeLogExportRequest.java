package net.nemerosa.ontrack.extension.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.split;

/**
 * Request for the export of a change log.
 * <p>
 * Additionally to the {@linkplain net.nemerosa.ontrack.extension.api.model.BuildDiffRequest change log request},
 * this request defines how to {@linkplain #grouping group} issues by type and which {@linkplain #format format}
 * to use for the export.
 * <p>
 * If no {@link #grouping grouping} is specified, the issues will be returned as a list, without any grouping.
 * <p>
 * The {@link #exclude exclude} field is used to exclude some issues from the export using their type.
 * <p>
 * Note that both the export format and the type of issues are specific notions linked to the issue service
 * (JIRA, GitHub...) that produces the issues.
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
     * Any issue that would not be mapped in a group will be assigned arbitrarily to the group defined by
     * {@link #altGroup}.
     * <p>
     * If the specification is empty, no grouping will be done.
     */
    private String grouping = "";

    /**
     * Comma separated list of issue types to exclude from the export.
     */
    private String exclude = "";

    /**
     * Title of the group to use when an issue does not belong to any group. It defaults to "Other".
     * If left empty, any issue not belonging to a group would be excluded from the export. This field
     * is not used when no grouping is specified.
     */
    private String altGroup = "Other";

    /**
     * Parses the specification and returns a map of groups x set of types. The map will be empty
     * if no group is defined, but never null.
     */
    @JsonIgnore
    public Map<String, Set<String>> getGroupingSpecification() {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        if (!StringUtils.isBlank(grouping)) {
            String[] groups = split(grouping, '|');
            for (String group : groups) {
                String[] groupSpec = split(group.trim(), '=');
                if (groupSpec.length != 2) throw new ExportRequestGroupingFormatException(grouping);
                String groupName = groupSpec[0].trim();
                result.put(
                        groupName,
                        Sets.newLinkedHashSet(
                                Arrays.asList(
                                        split(groupSpec[1].trim(), ',')
                                ).stream()
                                        .map(String::trim)
                                        .collect(Collectors.toList())
                        )
                );
            }
        }
        return result;
    }

    /**
     * Parses the comma-separated list of excluded types.
     */
    @JsonIgnore
    public Set<String> getExcludedTypes() {
        if (StringUtils.isBlank(exclude)) {
            return Collections.emptySet();
        } else {
            return Sets.newHashSet(
                    Arrays.asList(
                            StringUtils.split(exclude, ",")
                    ).stream()
                            .map(StringUtils::trim)
                            .collect(Collectors.toList())
            );
        }
    }
}
