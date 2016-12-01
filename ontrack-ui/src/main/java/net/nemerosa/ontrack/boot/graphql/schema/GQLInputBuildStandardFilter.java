package net.nemerosa.ontrack.boot.graphql.schema;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.schema.GraphQLInputObjectField;
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
                .field(formField("sincePromotionLevel", "Builds since the last one which was promoted to this level"))
                .field(formField("withPromotionLevel", "Builds with this promotion level"))
                .field(formField("afterDate", "Build created after or on this date"))
                .field(formField("beforeDate", "Build created before or on this date"))
                .field(formField("sinceValidationStamp", "Builds since the last one which had this validation stamp"))
                .field(formField("sinceValidationStampStatus", "... with status"))
                .field(formField("withValidationStamp", "Builds with this validation stamp"))
                .field(formField("withValidationStampStatus", "... with status"))
                .field(formField("withProperty", "With property"))
                .field(formField("withPropertyValue", "...with value"))
                .field(formField("sinceProperty", "Since property"))
                .field(formField("sincePropertyValue", "...with value"))
                // FIXME String linkedFrom;
                // FIXME String linkedTo;
                .build();
    }

    private GraphQLInputObjectField formField(String fieldName, String description) {
        return newInputObjectField()
                .name(fieldName)
                .description(description)
                .type(GraphQLString)
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
