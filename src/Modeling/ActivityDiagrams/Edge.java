package Modeling.ActivityDiagrams;

public class Edge {
	private String id;
	private String name;
	private String guard;
	private EdgeType type;
	private Activity source;
	private Activity target;

	public Edge(String id, String name, String type) {
		this.id = id;
		this.name = name;
		if (type.equals("uml:ControlFlow")) {
			this.type = EdgeType.controlFlow;
		} else if (type.equals("uml:ObjectFlow")) {
			this.type = EdgeType.objectFlow;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public Activity getSource() {
		return source;
	}

	public void setSource(Activity source) {
		this.source = source;
	}

	public Activity getTarget() {
		return target;
	}

	public void setTarget(Activity target) {
		this.target = target;
	}

	public String getId() {
		return id;
	}

	public EdgeType getType() {
		return type;
	}
	
	public void print() {
		System.out.println("Type: " + this.type + "; Name: " + this.name + "; Guard: " + this.guard);
	}
}
