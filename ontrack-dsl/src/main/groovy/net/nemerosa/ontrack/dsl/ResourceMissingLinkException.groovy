package net.nemerosa.ontrack.dsl

class ResourceMissingLinkException extends DSLException {
    def ResourceMissingLinkException(String link) {
        super("Link ${link} is not available. Some authorisations might be missing or the link name is mispelled.")
    }
}
