package graphs;

public class Edge {
	private Node nodeFrom;
	private Node nodeTo;
	private double cost;

	public Edge(Node nodeFrom, Node nodeTo, double cost) {
		this.nodeFrom = nodeFrom;
		this.nodeTo = nodeTo;
		this.cost = cost;
	}

	public Node getNodeFrom() {
		return nodeFrom;
	}

	public Node getNodeTo() {
		return nodeTo;
	}

	public double getCost() {
		return cost;
	}

	@Override
	public String toString() {
		return "Edge [nodeFrom=" + nodeFrom + ", nodeTo=" + nodeTo + ", cost=" + cost + "]";
	}
}
