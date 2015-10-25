package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.model.BuildPathMatchingException;
import net.nemerosa.ontrack.extension.svn.model.UnknownBuildPathExpression;
import net.nemerosa.ontrack.model.structure.Build;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVNUtils {

    public static final String BUILD_PLACEHOLDER_PATTERN = "\\{(.+)\\}";

    public static SVNURL toURL(String path) {
        try {
            return SVNURL.parseURIDecoded(path);
        } catch (SVNException e) {
            throw new IllegalArgumentException("Cannot get SVN URL for " + path, e);
        }
    }

    public static SVNURL toURL(String url, String path) {
        SVNURL repoURL = toURL(url);
        try {
            return repoURL.setPath(path, false);
        } catch (SVNException e) {
            throw new IllegalArgumentException("Cannot get SVN URL for " + path, e);
        }
    }

    /**
     * See the definition of the build path at {@link net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty#buildPath}.
     */
    @Deprecated
    public static String expandBuildPath(String buildPathDefinition, Build build) {
        return expandBuildPath(buildPathDefinition, build.getName());
    }

    /**
     * See the definition of the build path at {@link net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty#buildPath}.
     */
    @Deprecated
    public static String expandBuildPath(String buildPathDefinition, String name) {
        // Pattern
        Pattern pattern = Pattern.compile(BUILD_PLACEHOLDER_PATTERN);
        Matcher matcher = pattern.matcher(buildPathDefinition);
        StringBuffer path = new StringBuffer();
        while (matcher.find()) {
            String replacement = expandBuildPathExpression(matcher.group(1), name);
            matcher.appendReplacement(path, replacement);
        }
        matcher.appendTail(path);
        // OK
        return path.toString();
    }

    @Deprecated
    public static String expandBuildPathExpression(String expression, String name) {
        if ("build".equals(expression)) {
            return name;
        } else if (StringUtils.startsWith(expression, "build:")) {
            String pattern = StringUtils.substringAfter(expression, "build:");
            if (buildPatternOk(pattern, name)) {
                return name;
            } else {
                throw new BuildPathMatchingException(name, pattern);
            }
        } else {
            throw new UnknownBuildPathExpression(expression);
        }
    }

    protected static boolean buildPatternOk(String pattern, String value) {
        return Pattern.matches(
                pattern.replace(".", "\\.").replace("*", ".+"),
                value
        );
    }

}
