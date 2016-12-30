package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeGlobalRole implements GQLType {

    public static final String GLOBAL_ROLE = "GlobalRole";

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(GLOBAL_ROLE)
                .field(GraphqlUtils.stringField("id", "ID of the role"))
                .field(GraphqlUtils.nameField("Unique name for the role"))
                .field(GraphqlUtils.stringField("description", "Description of the role"))
                .build();
    }

}
