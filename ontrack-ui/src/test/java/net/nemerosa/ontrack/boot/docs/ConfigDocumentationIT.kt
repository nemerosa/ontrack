package net.nemerosa.ontrack.boot.docs

import net.nemerosa.ontrack.common.camelCaseToEnvironmentName
import net.nemerosa.ontrack.common.camelCaseToKebabCase
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmName

@Disabled("To be launched manually")
class ConfigDocumentationIT : AbstractDocumentationGenerationTestSupport() {

    @Test
    fun `Configuration properties`() {
        val configurations = findAllBeansAnnotatedWith(ConfigurationProperties::class)
            .filter {
                it::class.jvmName.startsWith("net.nemerosa.ontrack")
            }
            .filterNot {
                it::class.hasAnnotation<DocumentationIgnore>()
            }

        withDirectory("configurations") {

            writeFile(
                fileName = "index",
            ) { s ->
                for (configuration in configurations) {
                    s.append("include::${configuration::class.jvmName}.adoc[]\n\n")
                }
            }

            configurations.forEach { configuration ->
                generateConfiguration(this, configuration)
            }

        }
    }

    private fun generateConfiguration(
        directoryContext: DirectoryContext,
        configuration: Any
    ) {
        println("Generation docs for configuration $configuration")

        val id = configuration::class.jvmName
        val name = getAPITypeName(configuration::class)

        directoryContext.writeFile(
            fileId = id,
            level = 4,
            title = name,
        ) { s ->
            // Description
            val description = getAPITypeDescription(configuration::class)
            if (!description.isNullOrBlank()) {
                s.append("\n").append(description).append("\n")
            }

            // Fields
            s.append("\n")
            s.append("|===\n")
            s.append("| Name | Environment | Description | Default value | Notes\n")
            writeProperties(s, directoryContext, configuration, configuration)
            s.append("|===\n")

        }
    }

    private fun writeProperties(
        s: StringBuilder,
        directoryContext: DirectoryContext,
        configuration: Any,
        current: Any,
        prefix: String = "",
    ) {
        val classPrefix = configuration::class.findAnnotation<ConfigurationProperties>()
            ?.prefix
            ?: error("$configuration is not annotated with ConfigurationProperties")
        val properties = current::class.memberProperties.filter {
            it.visibility == KVisibility.PUBLIC
        }

        val deprecatedReason = current::class.findAnnotation<Deprecated>()?.message
            ?.let { "Deprecated: $it" }
            ?: ""

        for (member in properties) {

            val baseName = if (prefix.isBlank()) {
                member.name
            } else {
                "$prefix.${member.name}"
            }
            val qualifiedName = "$classPrefix.$baseName"

            val propertyName = qualifiedName.camelCaseToKebabCase()
            val envName = qualifiedName.camelCaseToEnvironmentName()

            if (isScalarProperty(member)) {
                val description = getPropertyDescription(member)

                val defaultValue = member.call(current)
                    ?.toString()
                    ?: ""

                writeProperty(s, propertyName, envName, description, defaultValue, deprecatedReason)
            } else {
                val child = try {
                    member.call(current)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
                    ?: error("Cannot get documentation for undefined children: $member [configuration = $configuration, prefix = $prefix")

                if (child is Map<*, *>) {
                    val kType = member.returnType       // KType for Map<String, MyClass>
                    // Extract type arguments
                    val (keyArg, valueArg) = kType.arguments
                    val keyType = keyArg.type              // KType for String
                    val valueType = valueArg.type          // KType for MyClass

                    val keyClass = keyType?.classifier
                    if (keyClass == String::class) {
                        val valueClass = valueType?.classifier as? KClass<*>
                        val instance = valueClass?.createInstance()
                            ?: error("Cannot generate documentation because cannot instantiate class: $member [configuration = $configuration, prefix = $prefix")

                        val description = getPropertyDescription(member)

                        s.append("\n")
                        s.append("|`").append(propertyName).append(".<*>").append("`\n")
                        s.append("|`").append("-").append("`\n")
                        s.append("|").append(description).append("\n")
                        s.append("|").append("_Empty_").append("\n")
                        s.append("|").append(deprecatedReason).append("\n")

                        writeProperties(
                            s = s,
                            directoryContext = directoryContext,
                            configuration = configuration,
                            current = instance,
                            prefix = "$prefix.${member.name}.<*>",
                        )
                    } else {
                        error("Cannot generate documentation for maps where the key is not a String: $member")
                    }
                } else if (child is List<*>) {
                    val kType = member.returnType       // KType for List<MyClass>
                    val (valueArg) = kType.arguments
                    val valueType = valueArg.type          // KType for MyClass
                    val valueClass = valueType?.classifier as? KClass<*>
                    val instance = valueClass?.createInstance()
                        ?: error("Cannot generate documentation because cannot instantiate class: $member [configuration = $configuration, prefix = $prefix")

                    if (isScalarClass(valueClass)) {
                        val description = getPropertyDescription(member)
                        writeProperty(
                            s = s,
                            propertyName = "$propertyName.<0>",
                            envName = "${envName}_<0>",
                            description = description,
                            defaultValue = "_Empty list_",
                            deprecatedReason = deprecatedReason,
                        )
                    } else {
                        writeProperties(
                            s = s,
                            directoryContext = directoryContext,
                            configuration = configuration,
                            current = instance,
                            prefix = "$prefix.${member.name}.<*>",
                        )
                    }
                } else if (child is Enum<*>) {
                    writeEnumProperty(
                        s = s,
                        propertyName = propertyName,
                        envName = envName,
                        member = member,
                        current = child,
                        deprecatedReason = deprecatedReason,
                    )
                } else if (child is Duration) {
                    writeDurationProperty(
                        s = s,
                        propertyName = propertyName,
                        envName = envName,
                        member = member,
                        current = child,
                        deprecatedReason = deprecatedReason,
                    )
                } else {
                    writeProperties(
                        s = s,
                        directoryContext = directoryContext,
                        configuration = configuration,
                        current = child,
                        prefix = if (prefix.isBlank()) {
                            member.name
                        } else {
                            "$prefix.${member.name}"
                        },
                    )
                }
            }
        }
    }

    private fun writeDurationProperty(
        s: StringBuilder,
        propertyName: String,
        envName: String,
        member: KProperty1<out Any, *>,
        current: Duration,
        deprecatedReason: String,
    ) {
        val description = getPropertyDescription(member)

        var defaultValue = current.toString()
        val durationUnit = member.findAnnotation<DurationUnit>()
        if (durationUnit != null) {
            val unit = durationUnit.value
            defaultValue += " (${unit.name})"
        }

        writeProperty(s, propertyName, envName, description, defaultValue, deprecatedReason)
    }

    private fun writeEnumProperty(
        s: StringBuilder,
        propertyName: String,
        envName: String,
        member: KProperty1<out Any, *>,
        current: Enum<*>,
        deprecatedReason: String,
    ) {
        val description = getPropertyDescription(member)

        val defaultValue = current.name

        writeProperty(s, propertyName, envName, description, defaultValue, deprecatedReason)
    }

    private fun writeProperty(
        s: StringBuilder,
        propertyName: String,
        envName: String,
        description: String,
        defaultValue: String,
        deprecatedReason: String,
    ) {
        s.append("\n")
        s.append("|`").append(propertyName).append("`\n")
        s.append("|`").append(envName).append("`\n")
        s.append("|").append(description).append("\n")
        s.append("|`").append(defaultValue).append("`\n")
        s.append("|").append(deprecatedReason).append("\n")
    }

    fun isScalarProperty(prop: KProperty<*>): Boolean {
        val classifier = prop.returnType.classifier
        return classifier is KClass<*> && isScalarClass(classifier)
    }

    fun isScalarClass(classifier: KClass<*>): Boolean = classifier in SCALAR_CLASSES

    companion object {
        private val SCALAR_CLASSES: Set<KClass<*>> = setOf(
            Boolean::class,
            Byte::class,
            Short::class,
            Int::class,
            Long::class,
            Float::class,
            Double::class,
            Char::class,
            String::class,
        )
    }

}