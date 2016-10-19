package net.nemerosa.ontrack.boot.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/graphql")
public class GraphqlController {

    private final GraphQLSchema schema;

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Autowired
    public GraphqlController(GraphQLSchema schema) {
        this.schema = schema;
    }

    /**
     * GET end point
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<JsonNode> get(
            @RequestParam String query,
            @RequestParam(required = false) String variables,
            @RequestParam(required = false) String operationName
    ) throws IOException {
        // Parses the arguments
        Map<String, Object> arguments = decodeIntoMap(variables);
        // Runs the query
        // TODO Execution strategy
        ExecutionResult executionResult = new GraphQL(schema).execute(query, operationName, null, arguments);
        // As JSON
        return ResponseEntity.ok(
                objectMapper.valueToTree(executionResult)
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> decodeIntoMap(String variablesParam) throws IOException {
        if (StringUtils.isNotBlank(variablesParam)) {
            return objectMapper.readValue(variablesParam, Map.class);
        } else {
            return Collections.emptyMap();
        }
    }

}
