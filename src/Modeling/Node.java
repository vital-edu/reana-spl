package Modeling;

import java.util.ArrayList;

public abstract class Node {
	private String id;
	private float execTime;
	private float energy;
	private float prob;
	protected boolean hasProb = false;
	protected boolean hasExecTime = false;
	protected boolean hasEnergy = false;
	protected Node nextLoopIteration;
	protected ArrayList<Node> loops = new ArrayList<Node>();

	public Node(String id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Node other = (Node) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	protected String getBaseID() {
		if (!this.id.contains("(loop")) {
			return this.id;
		}
		return this.id.substring(0, this.id.indexOf("(loop"));
	}

	public float getEnergy() {
		return this.energy;
	}

	public float getExecTime() {
		return this.execTime;
	}

	public String getId() {
		return this.id;
	}

	public Node getNextLoopIteration(Fragment f) {
		return this.nextLoopIteration;
	}

	/*
	 * public int getOrder() { return this.order; }
	 */

	public boolean hasEnergy() {
		return this.hasEnergy;
	}

	public boolean hasExecTime() {
		return this.hasExecTime;
	}

	public int hashCode() {
		// int prime = 31;
		int result = 1;
		result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
		return result;
	}

	public boolean hasProb() {
		return this.hasProb;
	}

	public float getProb() {
		return this.prob;
	}

	public void setProb(float prob) {
		this.hasProb = true;
		this.prob = prob;
	}

	public String newID(Fragment f) {
		if (!this.id.contains("_" + f.getBaseID() + ")")) {
			return this.id + "(loop0_" + f.getBaseID() + ")";
		}
		String tmp = this.id;
		String[] tokens = tmp.split("_" + f.getBaseID());
		int loopNumber = Integer.parseInt(tokens[0].substring(tokens[0].lastIndexOf("(loop") + 5,
				tokens[0].length()));
		String oldLoop = "(loop" + loopNumber + "_" + f.getBaseID() + ")";
		String newLoop = "(loop" + (loopNumber + 1) + "_" + f.getBaseID() + ")";
		tmp = tmp.replace(oldLoop, newLoop);
		return tmp;
	}

	public void print() {
		System.out.println("Node: " + this.id + " duration: " + this.execTime + " prob: "
				+ this.prob + " energy: " + this.energy + " Class: " + getClass());

		/*
		 * if (this.hasEnergy) { System.out.println("Node: " + this.id +
		 * " order: " + this.order + " duration: " + this.execTime + " Class: "
		 * + getClass() + " Energy: " + getEnergy()); } else {
		 * System.out.println("Node: " + this.id + " order: " + this.order +
		 * " duration: " + this.execTime + " Class: " + getClass()); }
		 */
	}

	public void setEnergy(float energy) {
		this.hasEnergy = true;
		this.energy = energy;
	}

	public void setExecTime(float execTime) {
		this.hasExecTime = true;
		this.execTime = execTime;
	}

	/*
	 * public void setOrder(int o) { this.order = o; }
	 */
}
