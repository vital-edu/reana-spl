package parsing;

public abstract class Node {
	private String id;
	private float execTime;
	private float energy;
	private float prob;
	protected boolean hasProb = false;
	protected boolean hasExecTime = false;
	protected boolean hasEnergy = false;

	public Node(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
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

	@Override
	public int hashCode() {
	    if (this.id != null) {
	        return (this.id + this.getClass()).hashCode();
	    } else {
	        return 0;
	    }
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

	public boolean hasEnergy() {
		return this.hasEnergy;
	}

	public boolean hasExecTime() {
		return this.hasExecTime;
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

	public void print() {
		System.out.println("Node: " + this.id + " duration: " + this.execTime + " prob: "
				+ this.prob + " energy: " + this.energy + " Class: " + getClass());
	}

	public void setEnergy(float energy) {
		this.hasEnergy = true;
		this.energy = energy;
	}

	public void setExecTime(float execTime) {
		this.hasExecTime = true;
		this.execTime = execTime;
	}
}
