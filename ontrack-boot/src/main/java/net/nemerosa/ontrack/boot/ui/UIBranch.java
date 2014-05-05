package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.boot.resource.Resource.link;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/ui/branches")
public class UIBranch {

    private final ResourceAssembler resourceAssembler;

    @Autowired
    public UIBranch(ResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
    }

    /**
     * FIXME List of branches for a project
     */
    @RequestMapping(value = "/projects/{project}/branches", method = RequestMethod.GET)
    public Resource<List<Resource<Branch>>> getBranchesForProject(@PathVariable String project) {
        List<Branch> branches = Collections.emptyList();
        return Resource.of(
                branches
                        .stream()
                        .map(resourceAssembler::toBranchResource)
                        .collect(Collectors.toList())
        )
                .self(link(fromMethodCall(on(UIBranch.class).getBranchesForProject(project))))
                ;
    }

    /**
     * FIXME Gets a branch.
     */
    @RequestMapping(value = "/branches/{id}", method = RequestMethod.GET)
    public Resource<Branch> getBranch(@PathVariable String id) {
        Branch branch = new Branch(id, id, id);
        return resourceAssembler.toBranchResource(branch);
    }

}
