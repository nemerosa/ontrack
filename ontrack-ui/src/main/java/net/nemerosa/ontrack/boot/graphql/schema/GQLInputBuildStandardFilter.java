package net.nemerosa.ontrack.boot.graphql.schema;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.structure.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

@Component
public class GQLInputBuildStandardFilter {

    private final BuildFilterService buildFilterService;

    @Autowired
    public GQLInputBuildStandardFilter(BuildFilterService buildFilterService) {
        this.buildFilterService = buildFilterService;
    }

    public GraphQLInputType getInputType() {
        // TODO See how to reuse the Form object
        return GraphQLInputObjectType.newInputObject()
                .name("StandardBuildFilter")
                .field(
                        newInputObjectField()
                                .name("count")
                                .description("Maximum number of builds to display")
                                .type(GraphQLInt)
                                .defaultValue(10)
                                .build()
                )
                // FIXME String sincePromotionLevel;
                // FIXME String withPromotionLevel;
                // FIXME LocalDate afterDate;
                // FIXME LocalDate beforeDate;
                // FIXME String sinceValidationStamp;
                // FIXME String sinceValidationStampStatus;
                .field(
                        newInputObjectField()
                                .name("withValidationStamp")
                                .description("Builds with this validation stamp")
                                .type(GraphQLString)
                                .defaultValue(null)
                                .build()
                )
                // FIXME String withValidationStampStatus;
                // FIXME String withProperty;
                // FIXME String withPropertyValue;
                // FIXME String sinceProperty;
                // FIXME String sincePropertyValue;
                // FIXME String linkedFrom;
                // FIXME String linkedTo;
                .build();
    }

    public BuildFilter parseMap(Branch branch, Map<String, ?> map) {
        JsonNode node = JsonUtils.fromMap(map);
        return buildFilterService.standardFilter(
                branch.getId(),
                node
        );
    }
}
