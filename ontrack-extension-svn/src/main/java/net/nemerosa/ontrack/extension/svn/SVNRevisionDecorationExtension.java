package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.extension.svn.service.SVNChangeLogService;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.OptionalLong;

@Component
public class SVNRevisionDecorationExtension extends AbstractExtension implements DecorationExtension {

    private final SVNChangeLogService svnChangeLogService;

    @Autowired
    public SVNRevisionDecorationExtension(SVNExtensionFeature extensionFeature, SVNChangeLogService svnChangeLogService) {
        super(extensionFeature);
        this.svnChangeLogService = svnChangeLogService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.BUILD);
    }

    @Override
    public Decoration getDecoration(ProjectEntity entity) {
        if (entity instanceof Build) {
            // Gets the revision for this build
            OptionalLong revision = svnChangeLogService.getBuildRevision((Build) entity);
            if (revision.isPresent()) {
                return Decoration.of(
                        this,
                        "revision",
                        String.valueOf(revision)
                );
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Expecting build");
        }
    }

}
