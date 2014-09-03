package net.nemerosa.ontrack.extension.issues.export;

import lombok.Data;

/**
 * Definition of an export format for the issues.
 */
@Data
public class ExportFormat {

    public static final ExportFormat TEXT = new ExportFormat("text", "Text", "text/plain");
    public static final ExportFormat HTML = new ExportFormat("html", "HTML", "text/html");

    private final String id;
    private final String name;
    private final String type;

}
