package net.nemerosa.ontrack.dsl.v4;

public class ResourceMissingLinkException extends DSLException {
    public ResourceMissingLinkException(String link) {
        super(
                String.format(
                        "Link %s is not available. Some authorisations might be missing or the link name is mispelled.",
                        link
                )
        );
    }
}
