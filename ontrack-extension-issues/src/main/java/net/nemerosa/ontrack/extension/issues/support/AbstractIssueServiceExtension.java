package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.model.ExportFormat;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.support.AbstractExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Convenient implementation for most of the issue services.
 */
public abstract class AbstractIssueServiceExtension extends AbstractExtension implements IssueServiceExtension {

    private final String id;
    private final String name;

    /**
     * Constructor.
     *
     * @param id   The unique ID for this service.
     * @param name The display name for this service.
     */
    protected AbstractIssueServiceExtension(ExtensionFeature extensionFeature, String id, String name) {
        super(extensionFeature);
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean containsIssueKey(IssueServiceConfiguration issueServiceConfiguration, String key, Set<String> keys) {
        return keys.contains(key);
    }

    /**
     * Export of both text and HTML by default.
     */
    @Override
    public List<ExportFormat> exportFormats() {
        return Arrays.asList(
                ExportFormat.TEXT,
                ExportFormat.HTML
        );
    }

}
