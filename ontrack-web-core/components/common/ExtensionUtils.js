export function getExtensionShortName(fqcn) {
    return fqcn.slice("net.nemerosa.ontrack.extension.".length)
}
