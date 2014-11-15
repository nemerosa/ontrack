package net.nemerosa.ontrack.extension.git.service;

import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLink;
import net.nemerosa.ontrack.extension.git.model.BuildGitCommitLinkNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildGitCommitLinkServiceImpl implements BuildGitCommitLinkService {

    private final List<BuildGitCommitLink<?>> links;

    @Autowired
    public BuildGitCommitLinkServiceImpl(List<BuildGitCommitLink<?>> links) {
        // TODO Fetches extensions
        this.links = links;
    }

    @Override
    public List<BuildGitCommitLink<?>> getLinks() {
        return links;
    }

    @Override
    public BuildGitCommitLink<?> getLink(String id) {
        return links.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElseThrow(() -> new BuildGitCommitLinkNotFoundException(id));
    }
}
