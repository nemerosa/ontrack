package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLTypeAccountGroup implements GQLType {

    public static final String ACCOUNT_GROUP = "AccountGroup";

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(ACCOUNT_GROUP)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.nameField())
                .field(GraphqlUtils.descriptionField())
                .build();
    }
}
