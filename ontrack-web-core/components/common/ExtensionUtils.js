export function getExtensionShortName(fqcn) {
    return fqcn.slice("net.nemerosa.ontrack.extension.".length)
}

export function getUserMenuItemExtensionName(id) {
    return id.slice("extension/".length)
}
