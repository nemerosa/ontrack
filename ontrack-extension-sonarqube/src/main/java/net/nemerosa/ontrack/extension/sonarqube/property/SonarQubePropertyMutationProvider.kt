package net.nemerosa.ontrack.extension.sonarqube.property

import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.graphql.schema.*
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.PropertyType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class SonarQubePropertyMutationProvider(
    private val sonarQubeConfigurationService: SonarQubeConfigurationService,
) : PropertyMutationProvider<SonarQubeProperty> {

    override val propertyType: KClass<out PropertyType<SonarQubeProperty>> = SonarQubePropertyType::class
    override val mutationNameFragment: String = "SonarQube"

    override val inputFields: List<GraphQLInputObjectField> = listOf(
        requiredStringInputField("configuration", "Name of the SonarQube configuration to use"),
        requiredStringInputField("key", "Key of the project in SonarQube"),
        requiredStringInputField("validationStamp", "Validation stamp to listen to"),
        requiredStringListInputField("measures", "List of measures to collect"),
        requiredBooleanInputField("override", "Overriding the list of measures from the global settings"),
        requiredBooleanInputField("branchModel", "Using the branch model to restrict the collection of measures"),
        optionalStringInputField(
            "branchPattern",
            "Regex to use to restrict the branches for which the measures are collected"
        ),
        requiredBooleanInputField(SonarQubeProperty::validationMetrics.name, getPropertyDescription(SonarQubeProperty::validationMetrics))
    )

    override fun readInput(entity: ProjectEntity, input: MutationInput) = SonarQubeProperty(
        configuration = sonarQubeConfigurationService.getConfiguration(input.getRequiredInput("configuration")),
        key = input.getRequiredInput("key"),
        validationStamp = input.getRequiredInput("validationStamp"),
        measures = input.getRequiredInput("measures"),
        override = input.getRequiredInput("override"),
        branchModel = input.getRequiredInput("branchModel"),
        branchPattern = input.getInput("branchPattern"),
        validationMetrics = input.getRequiredInput(SonarQubeProperty::validationMetrics.name),
    )

}