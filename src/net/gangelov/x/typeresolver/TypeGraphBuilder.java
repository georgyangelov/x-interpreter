package net.gangelov.x.typeresolver;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.nodes.AssignmentNode;
import net.gangelov.x.ast.nodes.NameNode;
import net.gangelov.x.ast.nodes.NumberLiteralNode;
import net.gangelov.x.ast.nodes.StringLiteralNode;
import net.gangelov.x.ast.Visitor;
import net.gangelov.x.graph.Graph;
import net.gangelov.x.types.Type;

import java.util.HashMap;
import java.util.Map;

public class TypeGraphBuilder extends Visitor {
    private TypeGraph graph = new TypeGraph();
    private Map<ASTNode, TypeGraph.Node> ASTMap = new HashMap<>();

    // TODO: Move these somewhere else
    private static final Type Int = new Type("Int");
    private static final Type String = new Type("String");

    // TODO: Move somewhere else
    private final Map<String, TypeGraph.Node> nameMap = new HashMap<>();

    public TypeGraphBuilder() {
    }

    public TypeGraph getGraph() {
        return graph;
    }

    public Map<ASTNode, TypeGraph.Node> getASTMap() {
        return ASTMap;
    }

    public Map<String, TypeGraph.Node> getNameMap() {
        return nameMap;
    }

    public Void visit(NumberLiteralNode node, Void context) {
        ASTMap.put(node, graph.addNode(Int));

        return null;
    }

    public Void visit(StringLiteralNode node, Void context) {
        ASTMap.put(node, graph.addNode(String));

        return null;
    }

    public Void visit(NameNode node, Void context) {
        TypeGraph.Node graphNode = addName(node.name);

        ASTMap.put(node, graphNode);

        return null;
    }

    public Void visit(AssignmentNode node, Void context) {
        node.value.visit(this, context);

        TypeGraph.Node valueNode = ASTMap.get(node.value);

        valueNode.addEdge(addName(node.name), null);
        ASTMap.put(node, valueNode);

        return null;
    }

    private TypeGraph.Node addName(String name) {
        if (nameMap.containsKey(name)) {
            return nameMap.get(name);
        } else {
            TypeGraph.Node graphNode = graph.addNode(null);

            nameMap.put(name, graphNode);

            return graphNode;
        }
    }
}
