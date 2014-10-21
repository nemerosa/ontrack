package net.nemerosa.ontrack.extension.svn.template;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.support.AbstractTemplateSynchronisationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Component
public class SVNBranchesTemplateSynchronisationSource extends AbstractTemplateSynchronisationSource<SVNBranchesTemplateSynchronisationSourceConfig> {

    private final SVNExtensionFeature svnExtensionFeature;
    private final ExtensionManager extensionManager;

    @Autowired
    public SVNBranchesTemplateSynchronisationSource(SVNExtensionFeature svnExtensionFeature, ExtensionManager extensionManager) {
        super(SVNBranchesTemplateSynchronisationSourceConfig.class);
        this.svnExtensionFeature = svnExtensionFeature;
        this.extensionManager = extensionManager;
    }

    @Override
    public String getId() {
        return "svn-branches";
    }

    @Override
    public String getName() {
        return "SVN branches";
    }

    @Override
    public boolean isApplicable(Branch branch) {
        return extensionManager.isExtensionFeatureEnabled(svnExtensionFeature)
                // TODO Branch configured for SVN?
                ;
    }

    @Override
    public Form getForm(Branch branch) {
        return SVNBranchesTemplateSynchronisationSourceConfig.form();
    }

    @Override
    public List<String> getBranchNames(Branch branch, SVNBranchesTemplateSynchronisationSourceConfig config) {
        // TODO Gets the SVN configuration
        // GitConfiguration gitConfiguration = gitService.getBranchConfiguration(branch);
        // Inclusion predicate
        Predicate<String> filter = config.getFilter();
        // TODO Gets the list of branches
//        return gitService.getRemoteBranches(gitConfiguration).stream()
//                .filter(filter)
//                .sorted()
//                .collect(Collectors.toList());
        return Collections.emptyList();
    }

}
