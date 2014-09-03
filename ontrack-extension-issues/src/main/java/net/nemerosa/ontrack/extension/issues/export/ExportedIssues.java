package net.nemerosa.ontrack.extension.issues.export;

import lombok.Data;

/**
 * List of issues, exported as text for a given format.
 */
@Data
public class ExportedIssues {

    private final String format;
    private final String content;

}
