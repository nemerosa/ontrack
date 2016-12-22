package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import org.springframework.stereotype.Component;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

@Component
public class GQLInputBuildStandardFilter {

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
                .field(formField(
                        "linkedFrom",
                        "The build must be linked FROM the builds selected by the pattern.\n" +
                                "Syntax: PRJ:BLD where PRJ is a project name and BLD a build expression - " +
                                "with * as placeholder"
                ))
                .field(formField(
                        "linkedFromPromotion",
                        "The build must be linked FROM a build having this promotion (requires \"linkedFrom\")"
                ))
                .field(formField(
                        "linkedTo",
                        "The build must be linked TO the builds selected by the pattern.\n" +
                                "Syntax: PRJ:BLD where PRJ is a project name and BLD a build expression - " +
                                "with * as placeholder"
                ))
                .field(formField(
                        "linkedToPromotion",
                        "The build must be linked TO a build having this promotion (requires \"linkedTo\")"
                ))
                .build();
    }

    private GraphQLInputObjectField formField(String fieldName, String description) {
        return newInputObjectField()
                .name(fieldName)
                .description(description)
                .type(GraphQLString)
                .build();
    }

}
