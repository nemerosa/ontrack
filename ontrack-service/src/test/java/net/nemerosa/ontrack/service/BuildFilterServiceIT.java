package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.Branch;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class BuildFilterServiceIT extends AbstractITTestSupport {

    @Autowired
    private BuildFilterService buildFilterService;

    @Test
    public void copyToBranch() throws Exception {
        // Source branch
        Branch sourceBranch = doCreateBranch();
        // Target branch
        Branch targetBranch = doCreateBranch();
    }
}