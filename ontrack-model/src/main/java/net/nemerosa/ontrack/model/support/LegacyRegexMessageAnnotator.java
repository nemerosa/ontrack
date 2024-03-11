package net.nemerosa.ontrack.model.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @deprecated Will be removed in V5. Use RegexMessageAnnotator instead.
 */
@Deprecated
public class LegacyRegexMessageAnnotator extends AbstractMessageAnnotator {

    private final Pattern pattern;
    private final Function<String, MessageAnnotation> annotationFactory;

    public LegacyRegexMessageAnnotator(String pattern, Function<String, MessageAnnotation> annotationFactory) {
        this(Pattern.compile(pattern), annotationFactory);
    }

    public LegacyRegexMessageAnnotator(Pattern pattern, Function<String, MessageAnnotation> annotationFactory) {
        this.pattern = pattern;
        this.annotationFactory = annotationFactory;
    }

    @Override
    public Collection<MessageAnnotation> annotate(String text) {
        Collection<MessageAnnotation> annotations = new ArrayList<>();
        int start = 0;
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            int mStart = m.start();
            int mEnd = m.end();
            // Previous section
            if (mStart > start) {
                String previous = text.substring(start, mStart);
                annotations.add(MessageAnnotation.t(previous));
            }
            // Match
            String match = m.group();
            MessageAnnotation annotation = annotationFactory.apply(match);
            annotations.add(annotation);
            // Next
            start = mEnd;
        }
        // End
        if (start < text.length() - 1) {
            String reminder = text.substring(start);
            annotations.add(MessageAnnotation.t(reminder));
        }
        // OK
        return annotations;
    }
}
