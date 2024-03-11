package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.support.MessageAnnotation.Companion.t
import java.util.regex.Matcher

class RegexMessageAnnotator(
    private val regex: Regex,
    private val annotationFactory: (token: String) -> MessageAnnotation,
) : AbstractMessageAnnotator() {

    private val pattern = regex.toPattern()

    override fun annotate(text: String): Collection<MessageAnnotation> {
        val annotations = mutableListOf<MessageAnnotation>()
        var start = 0
        val m: Matcher = pattern.matcher(text)
        while (m.find()) {
            val mStart = m.start(1)
            val mEnd = m.end(1)
            // Previous section
            if (mStart > start) {
                val previous = text.substring(start, mStart)
                annotations.add(t(previous))
            }
            // Match
            val match = m.group(1)
            val annotation: MessageAnnotation = annotationFactory(match)
            annotations.add(annotation)
            // Next
            start = mEnd
        }

        // End
        if (start < text.length) {
            val reminder = text.substring(start)
            annotations.add(t(reminder))
        }

        // OK
        return annotations
    }

}