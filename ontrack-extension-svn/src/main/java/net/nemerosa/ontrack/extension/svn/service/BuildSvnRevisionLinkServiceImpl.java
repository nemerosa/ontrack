package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLink;
import net.nemerosa.ontrack.extension.svn.model.BuildSvnRevisionLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuildSvnRevisionLinkServiceImpl implements BuildSvnRevisionLinkService {

    private final List<BuildSvnRevisionLink<?>> links;

    @Autowired
    public BuildSvnRevisionLinkServiceImpl(List<BuildSvnRevisionLink<?>> links) {
        this.links = links;
    }

    @Override
    public List<BuildSvnRevisionLink<?>> getLinks() {
        return links;
    }

    @Override
    public Optional<BuildSvnRevisionLink<?>> getOptionalLink(String id) {
        return links.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst();
    }
}
