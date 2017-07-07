package net.gangelov.x.typeresolver;

import net.gangelov.x.ast.ASTNode;
import net.gangelov.x.ast.nodes.AssignmentNode;
import net.gangelov.x.ast.nodes.NameNode;
import net.gangelov.x.ast.nodes.NumberLiteralNode;
import net.gangelov.x.ast.nodes.StringLiteralNode;
import net.gangelov.x.ast.Visitor;
import net.gangelov.x.types.TypeEnvironment;

import java.util.HashMap;
import java.util.Map;

public class TypeGraphBuilder extends Visitor {
    private TypeGraph graph = new TypeGraph();
    private Map<ASTNode, TypeGraph.Node> ASTMap = new HashMap<>();

    // TODO: Move somewhere else
    private final Map<String, TypeGraph.Node> nameMap = new HashMap<>();

    private final TypeEnvironment types;

    public TypeGraphBuilder(TypeEnvironment types) {
        this.types = types;
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
        // TODO: Do not get this every time
        ASTMap.put(node, graph.addNode(types.getType("Int")));

        return null;
    }

    public Void visit(StringLiteralNode node, Void context) {
        // TODO: Do not get this every time
        ASTMap.put(node, graph.addNode(types.getType("String")));

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
