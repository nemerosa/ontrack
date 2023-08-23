package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.common.Document

interface SCMRefService {

    /**
     * Given a SCM Ref, returns a document.
     *
     * @param ref SCM reference
     * @param type [Document type][Document.type]
     */
    fun downloadDocument(ref: SCMRef, type: String): Document?

}