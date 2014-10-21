package net.nemerosa.ontrack.extension.svn.template;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.svn.SVNExtensionFeature;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.support.AbstractTemplateSynchronisationSource;
import net.nemerosa.ontrack.tx.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SVNBranchesTemplateSynchronisationSource extends AbstractTemplateSynchronisationSource<SVNBranchesTemplateSynchronisationSourceConfig> {

    private final SVNExtensionFeature svnExtensionFeature;
    private final ExtensionManager extensionManager;
    private final SVNService svnService;
    private final TransactionService transactionService;

    @Autowired
    public SVNBranchesTemplateSynchronisationSource(SVNExtensionFeature svnExtensionFeature, ExtensionManager extensionManager, SVNService svnService, TransactionService transactionService) {
        super(SVNBranchesTemplateSynchronisationSourceConfig.class);
        this.svnExtensionFeature = svnExtensionFeature;
        this.extensionManager = extensionManager;
        this.svnService = svnService;
        this.transactionService = transactionService;
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
                && svnService.getSVNRepository(branch).isPresent();
    }

    @Override
    public Form getForm(Branch branch) {
        return SVNBranchesTemplateSynchronisationSourceConfig.form();
    }

    @Override
    public List<String> getBranchNames(Branch branch, SVNBranchesTemplateSynchronisationSourceConfig config) {
        return transactionService.doInTransaction(() -> {
                    // Inclusion predicate
                    Predicate<String> filter = config.getFilter();
                    // Gets the list of branches
                    return svnService.getBranches(branch).stream()
                            .filter(filter)
                            .sorted()
                            .collect(Collectors.toList());
                }
        );
    }

}
