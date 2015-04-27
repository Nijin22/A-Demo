package graphs;

public class Node implements Comparable<Node> {
	private String name;
	private int posX;
	private int posY;
	private double costTillThisNode;
	private double pathfindingValue; // actual cost + heuristics, as set by the
	// pathfinding algorithm
	private Node previousNode;
	private boolean completed = false; // status of this node, true if no
										// further calculation is required and
										// pathfinding already found the ideal
										// path
	
	public boolean isOnBestPath = false;

	public Node(String name, int posX, int posY) throws Exception {
		if (name.contains("-")) {
			throw new Exception("- is not allowed in Node names.");
		} else {
			this.name = name;
			this.posX = posX;
			this.posY = posY;
			this.pathfindingValue = Double.MAX_VALUE;
			this.costTillThisNode = 0; // shouldn't matter
		}
	}

	public double getCostTillThisNode() {
		return costTillThisNode;
	}

	public void setCostTillThisNode(double costTillThisNode) {
		this.costTillThisNode = costTillThisNode;
	}

	public double getPathfindingValue() {
		return pathfindingValue;
	}

	public void setPathfindingValue(double cost) {
		this.pathfindingValue = cost;
	}

	public String getName() {
		return name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void markCompleted() {
		this.completed = true;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Node other) {
		// we're comparing the costs if we want to compare nodes with each other
		return Double.compare(this.pathfindingValue, other.pathfindingValue);
	}

}
