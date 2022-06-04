package net.nemerosa.ontrack.extension.av.config

/**
 * Description of an auto versioning configuration
 *
 * @property targetRegex Regex to use in the target file to identify the line to replace
 *                       with the new version. The first matching group must be the version.
 * @property targetProperty Optional replacement for the regex, using only a property name
 * @property targetPropertyRegex Optional regex to use on the [property][targetProperty] value
 * @property targetPropertyType When [targetProperty] is defined, defines the type of property (defaults to Java properties file, but could be NPM, etc.)
 */
interface AutoVersioningTargetConfig {
    val targetRegex: String?
    val targetProperty: String?
    val targetPropertyRegex: String?
    val targetPropertyType: String?
}