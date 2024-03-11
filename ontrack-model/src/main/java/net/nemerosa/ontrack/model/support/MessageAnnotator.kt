package net.nemerosa.ontrack.model.support

fun interface MessageAnnotator {

    fun annotate(text: String): Collection<MessageAnnotation>

}
