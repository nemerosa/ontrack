package net.nemerosa.ontrack.model.structure;

/**
 * Request for the copy of a branch configuration into another one.
 */
interface CopyRequest {
    val replacements: List<Replacement>
}
