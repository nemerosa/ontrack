package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.model.*;
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

    public static boolean isPathRevision(String pathPattern) {
        if (StringUtils.isNotBlank(pathPattern)) {
            return pathPattern.endsWith("@{build}");
        } else {
            throw new BuildPathNotDefinedException();
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

    public static String getBuildName(SVNLocation location, String pathPattern) {
        if (isPathRevision(pathPattern)) {
            // Removes the last part of the pattern
            String pathOnly = StringUtils.substringBeforeLast(pathPattern, "@");
            // Equality of paths is required
            if (StringUtils.equals(location.getPath(), pathOnly)) {
                return String.valueOf(location.getRevision());
            } else {
                throw new BuildPathMatchingException(location.getPath(), pathPattern);
            }
        } else {
            // Replaces the whole build expression by a generic regex
            String regex = pathPattern.replaceAll(BUILD_PLACEHOLDER_PATTERN, "(.*)");
            // Identifies the variable part in this regex
            Matcher matcher = Pattern.compile(regex).matcher(location.getPath());
            if (matcher.matches()) {
                String name = matcher.group(1);
                if (!StringUtils.isBlank(name)
                        && StringUtils.equals(location.getPath(), expandBuildPath(pathPattern, name))) {
                    return name;
                } else {
                    throw new BuildPathMatchingException(location.getPath(), pathPattern);
                }
            } else {
                throw new BuildPathMatchingException(location.getPath(), pathPattern);
            }
        }
    }

    public static boolean followsBuildPattern(SVNLocation location, String pathPattern) {
        try {
            return StringUtils.isNotBlank(getBuildName(location, pathPattern));
        } catch (BuildPathMatchingException ex) {
            return false;
        }
    }

    public static String getBasePath(String pathPattern) {
        if (isPathRevision(pathPattern)) {
            throw new NoBasePathForRevisionPatternException(pathPattern);
        } else {
            // Removes any expression
            String rawPath = pathPattern.replaceAll(BUILD_PLACEHOLDER_PATTERN, "");
            // Gets the path BEFORE the last slash
            return StringUtils.substringBeforeLast(rawPath, "/");
        }
    }
}
