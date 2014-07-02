package net.nemerosa.ontrack.extension.svn.support;

import net.nemerosa.ontrack.extension.svn.model.BuildPathMatchingException;
import net.nemerosa.ontrack.extension.svn.model.SVNLocation;
import net.nemerosa.ontrack.extension.svn.model.UnknownBuildPathExpression;
import net.nemerosa.ontrack.model.structure.Build;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVNUtils {

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
        return pathPattern.endsWith("@{build}");
    }

    public static boolean followsBuildPattern(SVNLocation location, String pathPattern) {
        if (isPathRevision(pathPattern)) {
            // Removes the last part of the pattern
            String pathOnly = StringUtils.substringBeforeLast(pathPattern, "@");
            // Equality of paths is required
            return StringUtils.equals(location.getPath(), pathOnly);
        } else {
            return Pattern.compile(StringUtils.replace(pathPattern, "*", ".+")).matcher(location.getPath()).matches();
        }
    }

    /**
     * See the definition of the build path at {@link net.nemerosa.ontrack.extension.svn.property.SVNBranchConfigurationProperty#buildPath}.
     */
    public static String expandBuildPath(String buildPathDefinition, Build build) {
        // Pattern
        Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(buildPathDefinition);
        StringBuffer path = new StringBuffer();
        while (matcher.find()) {
            String replacement = expandBuildPathExpression(matcher.group(1), build);
            matcher.appendReplacement(path, replacement);
        }
        matcher.appendTail(path);
        // OK
        return path.toString();
    }

    public static String expandBuildPathExpression(String expression, Build build) {
        // TODO Property expansion
        if ("build".equals(expression)) {
            return build.getName();
        } else if (StringUtils.startsWith(expression, "build:")) {
            String pattern = StringUtils.substringAfter(expression, "build:");
            if (buildPatternOk(pattern, build.getName())) {
                return build.getName();
            } else {
                throw new BuildPathMatchingException(build.getName(), pattern);
            }
        } else {
            throw new UnknownBuildPathExpression(expression);
        }
    }

    public static boolean buildPatternOk(String pattern, String value) {
        return Pattern.matches(
                pattern.replace(".", "\\.").replace("*", ".+"),
                value
        );
    }

}
