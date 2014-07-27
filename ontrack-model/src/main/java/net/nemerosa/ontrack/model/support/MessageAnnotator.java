package net.nemerosa.ontrack.model.support;

import java.util.Collection;

@FunctionalInterface
public interface MessageAnnotator {

    Collection<MessageAnnotation> annotate(String text);

}
