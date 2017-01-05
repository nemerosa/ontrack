package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.GraphQLObjectType;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import org.springframework.stereotype.Component;

import static graphql.schema.GraphQLObjectType.newObject;

/**
 * @see net.nemerosa.ontrack.model.security.AccountGroupMapping
 */
@Component
public class GQLTypeAccountGroupMapping implements GQLType {

    public static final String ACCOUNT_GROUP_MAPPING = "AccountGroupMapping";

    @Override
    public GraphQLObjectType getType() {
        return newObject()
                .name(ACCOUNT_GROUP_MAPPING)
                .field(GraphqlUtils.idField())
                .field(GraphqlUtils.stringField("name", "Name of the mapping"))
                .field(GraphqlUtils.stringField("type", "Type of the mapping"))
                .build();
    }

}
