package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;

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

}
