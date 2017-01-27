package net.nemerosa.ontrack.graphql.support;

import graphql.relay.*;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import java.util.ArrayList;
import java.util.List;

public class ConnectionList implements DataFetcher {

    private static final String DUMMY_CURSOR_PREFIX = "simple-cursor";
    private final List<?> data;

    public ConnectionList(List<?> data) {
        this.data = data;

    }

    private List<Edge> buildEdges() {
        List<Edge> edges = new ArrayList<>();
        int ix = 0;
        for (Object object : data) {
            edges.add(new Edge(object, new ConnectionCursor(createCursor(ix++))));
        }
        return edges;
    }


    @Override
    public Object get(DataFetchingEnvironment environment) {

        List<Edge> edges = buildEdges();

        ConnectionCursor firstPresliceCursor = edges.get(0).getCursor();
        ConnectionCursor lastPresliceCursor = edges.get(edges.size() - 1).getCursor();

        int afterOffset = getOffsetFromCursor(environment.getArgument("after"), -1);
        int begin = Math.max(afterOffset, -1) + 1;
        int beforeOffset = getOffsetFromCursor(environment.getArgument("before"), edges.size());
        int end = Math.min(beforeOffset, edges.size());

        edges = edges.subList(begin, end);
        if (edges.size() == 0) {
            return emptyConnection();
        }


        Integer first = environment.<Integer>getArgument("first");
        Integer last = environment.<Integer>getArgument("last");

        if (first != null) {
            edges = edges.subList(0, first <= edges.size() ? first : edges.size());
        }
        if (last != null) {
            edges = edges.subList(last > edges.size() ? 0 : edges.size() - last, edges.size());
        }

        if (edges.size() == 0) {
            return emptyConnection();
        }

        Edge firstEdge = edges.get(0);
        Edge lastEdge = edges.get(edges.size() - 1);

        PageInfo pageInfo = new PageInfo();
        pageInfo.setStartCursor(firstEdge.getCursor());
        pageInfo.setEndCursor(lastEdge.getCursor());
        pageInfo.setHasPreviousPage(!firstEdge.getCursor().equals(firstPresliceCursor));
        pageInfo.setHasNextPage(!lastEdge.getCursor().equals(lastPresliceCursor));

        Connection connection = new Connection();
        connection.setEdges(edges);
        connection.setPageInfo(pageInfo);

        return connection;
    }

    private Connection emptyConnection() {
        Connection connection = new Connection();
        connection.setPageInfo(new PageInfo());
        return connection;
    }

    private int getOffsetFromCursor(String cursor, int defaultValue) {
        if (cursor == null) return defaultValue;
        String string = Base64.fromBase64(cursor);
        return Integer.parseInt(string.substring(DUMMY_CURSOR_PREFIX.length()));
    }

    private String createCursor(int offset) {
        return Base64.toBase64(DUMMY_CURSOR_PREFIX + Integer.toString(offset));
    }


}