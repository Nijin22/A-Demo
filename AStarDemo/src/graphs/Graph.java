package graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Graph {
	private HashMap<String, Node> nodes = new HashMap<String, Node>();
	// key = string name

	private HashMap<String, Edge> edges = new HashMap<String, Edge>();
	// key = string node1 & "-" & node2

	private PriorityQueue<Node> openQueue = new PriorityQueue<Node>();

	private int statCheckedNodes = 0;

	public heuristicMode heuristicMode;

	public enum heuristicMode {
		INACTIVE, GEOMETRIC_APPROXIMATION_DIRECT
	}

	public Graph() {
		heuristicMode = heuristicMode.INACTIVE;
	}

	public int getStatCheckedNodes() {
		return statCheckedNodes;
	}

	public Collection<Node> getEdgingNodes(Node node) {
		HashSet<Node> hs = new HashSet<Node>();

		Iterator<Edge> itr = edges.values().iterator();
		while (itr.hasNext()) {
			Edge currentEdge = itr.next();
			if (currentEdge.getNodeFrom() == node) {
				hs.add(currentEdge.getNodeTo());
			}
		}
		return hs;
	}

	public Node getNodeByName(String name) {
		return nodes.get(name);
	}

	public Edge getEdgeByNodes(String node1Name, String node2Name) {
		return edges.get(node1Name + "-" + node2Name);
	}

	public Collection<Node> getNodes() {
		return nodes.values();
	}

	public Collection<Edge> getEdges() {
		return edges.values();
	}

	public void addNode(Node node) {
		nodes.put(node.getName(), node);
	}

	public void addEdge(Edge edge) {
		String edgeKey = edge.getNodeFrom().toString() + "-" + edge.getNodeTo().toString();
		edges.put(edgeKey, edge);
	}

	public void clear() {
		nodes = new HashMap<String, Node>();
		edges = new HashMap<String, Edge>();
	}

	@Override
	public String toString() {
		return "Graph [nodes=" + nodes + ", edges=" + edges + "]";
	}

	public Node performAStar(String startNodeName, String targetNodeName) throws NoPathExistsException {
		resetPathfindingData();

		// Add the start element
		Node startNode = getNodeByName(startNodeName);
		startNode.setCostTillThisNode(0); // The cost of the start node is
											// always 0
		openQueue.add(startNode);

		while (openQueue.isEmpty() == false) {
			// as long as we have some more nodes to check...

			statCheckedNodes++;

			Node currentNode = openQueue.poll(); // take the node with the
													// lowest pathfindingValue

			currentNode.hasBeenExpanded = true; // for demo purposes

			if (currentNode.getName() == targetNodeName) {
				// Success! We found a valid path!
				return currentNode;
			}

			// calculation for this node is completed.
			currentNode.markCompleted();

			// expanding this node to add its subsequent nodes to the open
			// list...
			for (Node successor : getEdgingNodes(currentNode)) {
				// for each subsequent node...
				if (successor.isCompleted()) {
					// no need to perform calculations on a node we already
					// completed.
					continue;
				}

				// cost till successor = costCurrent + cost of node connecting
				// both
				double costsTillSuccessor = currentNode.getCostTillThisNode()
						+ getEdgeByNodes(currentNode.getName(), successor.getName()).getCost();

				if (openQueue.contains(successor)) {
					if (costsTillSuccessor < successor.getCostTillThisNode()) {
						// we found a better path to this node, let's save this
						// instead!
						successor.setCostTillThisNode(costsTillSuccessor);
						successor.setPreviousNode(currentNode);
						successor.setPathfindingValue(costsTillSuccessor
								+ estimateCost(successor, getNodeByName(targetNodeName)));
					}
				} else {
					// successor not in open queue
					successor.setCostTillThisNode(costsTillSuccessor);
					successor.setPreviousNode(currentNode);
					successor.setPathfindingValue(costsTillSuccessor
							+ estimateCost(successor, getNodeByName(targetNodeName)));

					openQueue.add(successor);
				}
			}
		}

		// openQueue is empty --> No valid path exists
		throw new NoPathExistsException();
	}

	public void resetPathfindingData() {
		openQueue.clear();
		statCheckedNodes = 0;
		for (Node node : nodes.values()) {
			node.resetPathfindingData();
		}
	}

	public double estimateCost(Node from, Node to) {
		switch (heuristicMode) {
		case GEOMETRIC_APPROXIMATION_DIRECT:
			// please note that this mode could return invalid data if two notes
			// can be connected for less costs than their distance (according to
			// posX & posY)
			// this mode should be okay for graphs where the cost between two
			// notes = their distance
			System.out.println("Debug. " + from.getName() + " --> " + to.getName());
			System.out.println("  "
					+ Math.sqrt(Math.abs(from.getPosX() - to.getPosY()) + Math.abs(from.getPosY() - to.getPosY())));
			return Math.sqrt(Math.abs(from.getPosX() - to.getPosY()) + Math.abs(from.getPosY() - to.getPosY()));

		default: // or INACTIVE
			return 0;
		}
	}
}
