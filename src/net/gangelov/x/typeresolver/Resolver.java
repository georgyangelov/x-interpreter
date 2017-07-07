package net.gangelov.x.typeresolver;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.graph.Graph;
import net.gangelov.x.types.Type;

import java.util.*;

public class Resolver {
    private final TypeGraph typeGraph;
    private final Map<ASTNode, TypeGraph.Node> astMap;
    private final Map<String, TypeGraph.Node> nameMap;

    public Resolver(TypeGraphBuilder builder) {
        this.typeGraph = builder.getGraph();
        this.astMap = builder.getASTMap();
        this.nameMap = builder.getNameMap();
    }

    public void resolve() throws TypeError {
        Queue<TypeGraph.Node> queue = new ArrayDeque<>();

        typeGraph.nodes.stream()
                .filter(node -> node.data != null)
                .forEach(queue::add);

        TypeGraph.Node node;
        while ((node = queue.poll()) != null) {
            for (TypeGraph.Edge edge : node.edges) {
                TypeGraph.Node neighbour = edge.to;

                if (neighbour.data != null && neighbour.data != node.data) {
                    throw new TypeError(
                            "Type " + node.data.name + " cannot be assigned to " + neighbour.data.name
                    );
                }

                if (neighbour.data != node.data) {
                    neighbour.data = node.data;
                    queue.add(neighbour);
                }
            }
        }

        // TODO: Check for non-visited nodes
    }

    public Type typeOf(ASTNode node) {
        // TODO: Handle the case when the ASTNode is not in the astMap
        return astMap.get(node).data;
    }

    public Type typeOf(String name) {
        // TODO: Handle the case when the name is not in the nameMap
        return nameMap.get(name).data;
    }
}
