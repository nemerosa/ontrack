package net.nemerosa.ontrack.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.graphql.schema.GraphqlSchemaService;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

@Transactional
@RestController
@RequestMapping("/graphql")
public class GraphqlController {

    private final GraphqlSchemaService schemaService;

    private final ObjectMapper objectMapper = ObjectMapperFactory.create();

    @Autowired
    public GraphqlController(GraphqlSchemaService schemaService) {
        this.schemaService = schemaService;
    }

    /**
     * Request model
     */
    @Data
    public static class Request {
        private final String query;
        @Wither
        private final Map<String, Object> variables;
        private final String operationName;

        public Request withVariables() {
            if (variables == null) {
                return withVariables(Collections.emptyMap());
            } else {
                return this;
            }
        }
    }

    /**
     * GET end point
     */
    @RequestMapping(method = RequestMethod.GET)
    @Transactional
    public Callable<ResponseEntity<JsonNode>> get(
            @RequestParam String query,
            @RequestParam(required = false) String variables,
            @RequestParam(required = false) String operationName
    ) throws IOException {
        return () -> {
            // Parses the arguments
            Map<String, Object> arguments = decodeIntoMap(variables);
            // Runs the query
            return ResponseEntity.ok(
                    requestAsJson(
                            new Request(
                                    query,
                                    arguments,
                                    operationName
                            )
                    )
            );
        };
    }

    /**
     * POST end point
     */
    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public Callable<ResponseEntity<JsonNode>> post(@RequestBody String input) throws IOException {
        return () -> {
            // Gets the components
            Request request = objectMapper.readValue(input, Request.class);
            // Variables must not be null
            request = request.withVariables();
            // Runs the query
            return ResponseEntity.ok(
                    requestAsJson(request)
            );
        };
    }

    /**
     * Request execution (JSON)
     */
    public JsonNode requestAsJson(Request request) {
        return objectMapper.valueToTree(
                request(request)
        );

    }

    /**
     * Request execution
     */
    public ExecutionResult request(Request request) {
        // Schema
        GraphQLSchema schema = schemaService.getSchema();
        // TODO Execution strategy
        return new GraphQL(schema).execute(
                request.getQuery(),
                request.getOperationName(),
                null, // No context
                request.getVariables());

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
