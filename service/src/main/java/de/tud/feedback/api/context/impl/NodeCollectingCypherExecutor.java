package de.tud.feedback.api.context.impl;

import de.tud.feedback.api.context.CypherExecutor;
import de.tud.feedback.domain.Node;
import org.neo4j.ogm.session.result.Result;
import org.springframework.data.neo4j.template.Neo4jOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

public class NodeCollectingCypherExecutor implements CypherExecutor {

    private final List<Node> nodes = newArrayList();

    private final Neo4jOperations operations;

    public NodeCollectingCypherExecutor(Neo4jOperations operations) {
        this.operations = operations;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public void execute(String cypherQuery, Map<String, ?> params) {
        Result result = operations.query(cypherQuery, params);

        if (hasCreatedNodes(result)) {
            nodes.addAll(createdNodesFor(idsWithin(result)));
        }
    }

    private Collection<Node> createdNodesFor(List<Integer> ids) {
        return ids
                .stream()
                .map(id -> new Node(id.longValue()))
                .collect(toList());
    }

    private List<Integer> idsWithin(Result result) {
        List<Integer> ids = rowsFrom(result)
                .stream()
                .map(row -> (Integer) row.get("ID"))
                .distinct()
                .collect(toList());

        if (ids.size() != numberOfCreatedNodesFrom(result)) {
            throw new RuntimeException("You should return the ID for every generated node.");
        }

        return ids;
    }

    private Set<Map<String, Object>> rowsFrom(Result result) {
        return newHashSet(result.queryResults());
    }

    private int numberOfCreatedNodesFrom(Result result) {
        return result.queryStatistics().getNodesCreated();
    }

    private boolean hasCreatedNodes(Result result) {
        return numberOfCreatedNodesFrom(result) > 0;
    }

}
