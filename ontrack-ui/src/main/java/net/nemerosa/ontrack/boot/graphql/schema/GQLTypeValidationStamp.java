package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.relay.SimpleListConnection;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.boot.graphql.support.Relay;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.model.structure.ValidationRun;
import net.nemerosa.ontrack.model.structure.ValidationStamp;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeValidationStamp extends AbstractGQLProjectEntity<ValidationStamp> {

    public static final String VALIDATION_STAMP = "ValidationStamp";

    private final StructureService structureService;
    private final GQLTypeValidationRunStatusID validationRunStatusID;

    public GQLTypeValidationStamp(URIBuilder uriBuilder, SecurityService securityService, List<ResourceDecorator<?>> decorators, StructureService structureService, GQLTypeValidationRunStatusID validationRunStatusID) {
        super(uriBuilder, securityService, ValidationStamp.class, decorators);
        this.structureService = structureService;
        this.validationRunStatusID = validationRunStatusID;
    }

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(VALIDATION_STAMP)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // TODO Image
                // Validation runs
                .field(
                        newFieldDefinition()
                                .name("validationRuns")
                                .description("List of runs for this validation stamp")
                                // FIXME Validation run, not validation ID
                                .type(GraphqlUtils.connectionList(validationRunStatusID.getType()))
                                .argument(Relay.getConnectionFieldArguments())
                                .dataFetcher(validationStampValidationRunsFetcher())
                                .build()
                )
                // OK
                .build();

    }

    private DataFetcher validationStampValidationRunsFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (source instanceof ValidationStamp) {
                ValidationStamp validationStamp = (ValidationStamp) source;
                // Gets all the validation runs
                // TODO Use environment for limits?
                List<ValidationRun> validationRuns = structureService.getValidationRunsForValidationStamp(
                        validationStamp.getId(),
                        0,
                        Integer.MAX_VALUE
                );
                // As a connection list
                return new SimpleListConnection(validationRuns).get(environment);
            } else {
                return Collections.emptyList();
            }
        };
    }

}
