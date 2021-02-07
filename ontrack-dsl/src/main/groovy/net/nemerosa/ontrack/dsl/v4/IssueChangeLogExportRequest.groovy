package net.nemerosa.ontrack.dsl.v4

import groovy.transform.Canonical

@Canonical
class IssueChangeLogExportRequest {

    String format = 'text'
    List<String> exclude = []
    String altGroup = 'Other'
    Map<String, List<String>> groups = [:]

    Map<String, ?> toQuery(int from, int to) {
        [
                from    : from,
                to      : to,
                format  : format,
                exclude : exclude.join(','),
                altGroup: altGroup,
                grouping: groups.collect { name, types -> "${name}=${types.join(',')}" }.join('|')
        ]
    }
}
