package net.nemerosa.ontrack.model.structure

/**
 * What's the purpose of a token
 */
enum class TokenScope(
    val transient: Boolean,
    val unique: Boolean,
) {

    /**
     * Token for the a user (default)
     */
    USER(transient = false, unique = false),

    /**
     * Token used for Next UI
     */
    NEXT_UI(transient = true, unique = true),


}