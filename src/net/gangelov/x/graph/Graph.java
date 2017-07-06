package net.gangelov.x.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph<NodeData, EdgeData> {
    public class Edge {
        public final Node to;
        public EdgeData data;

        public Edge(Node to, EdgeData data) {
            this.to = to;
            this.data = data;
        }
    }

    public class Node {
        public List<Edge> edges = new ArrayList<>();
        public NodeData data;

        public Node(NodeData data) {
            this.data = data;
        }

        public Edge addEdge(Node other, EdgeData data) {
            Edge edge = new Edge(other, data);

            edges.add(edge);

            return edge;
        }
    }

    public List<Node> nodes = new ArrayList<>();

    public Graph() {
    }

    public Node addNode(NodeData data) {
        Node node = new Node(data);

        nodes.add(node);

        return node;
    }
}
