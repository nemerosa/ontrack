package net.nemerosa.ontrack.extension.scm.service;

import java.util.List;
import java.util.function.Predicate;

/**
 * Generic service around the SCM
 */
public interface SCMUtilsService {

    Predicate<String> getPathFilter(List<String> patterns);

}
