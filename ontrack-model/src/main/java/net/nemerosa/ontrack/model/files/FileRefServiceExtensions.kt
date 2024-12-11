package net.nemerosa.ontrack.model.files

import net.nemerosa.ontrack.common.Document

fun FileRefService.downloadDocument(uri: String, type: String): Document? {
    val ref = FileRef.parseUri(uri)
        ?: throw FileRefURIParsingException(uri)
    return downloadDocument(ref, type)
}
