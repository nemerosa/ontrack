package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.Document

fun SCMRefService.downloadDocument(uri: String, type: String): Document? {
    val ref = SCMRef.parseUri(uri)
        ?: throw SCMRefURIParsingException(uri)
    return downloadDocument(ref, type)
}
