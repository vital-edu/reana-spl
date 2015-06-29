package Modeling;

import java.util.ArrayList;

public class Activity {
	private String id;
	private String name;
	private ActivityType type;
	private ArrayList<Edge> incoming;
	private ArrayList<Edge> outgoing;
	private boolean ordered;
	private String sdID;
	private FragmentOld sd;
	
	public Activity(String id, String name, String type) {
		this.id = id;
		this.name = name;
		this.incoming = new ArrayList<Edge>();
		this.outgoing = new ArrayList<Edge>();
		this.ordered = false;
		if (type.equals("uml:InitialNode")) {
			this.type = ActivityType.initialNode;
		} else if (type.equals("uml:ActivityFinalNode")) {
			this.type = ActivityType.finalNode;
		} else if (type.equals("uml:CallBehaviorAction")) {
			this.type = ActivityType.call;
		} else if (type.equals("uml:DecisionNode")) {
			this.type = ActivityType.decision;
		} else if (type.equals("uml:MergeNode")) {
			this.type = ActivityType.merge;
		} else if (type.equals("uml:ForkNode")) {
			this.type = ActivityType.fork;
		} else if (type.equals("uml:JoinNode")) {
			this.type = ActivityType.fork;
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Edge> getIncoming() {
		return incoming;
	}

	public void setIncoming(ArrayList<Edge> incoming) {
		this.incoming = incoming;
	}
	
	public void addIncoming(Edge edge) {
		this.incoming.add(edge);
	}

	public ArrayList<Edge> getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(ArrayList<Edge> outgoing) {
		this.outgoing = outgoing;
	}
	
	public void addOutgoing(Edge edge) {
		this.outgoing.add(edge);
	}

	public String getId() {
		return id;
	}

	public ActivityType getType() {
		return type;
	}
	
	public boolean isOrdered() {
		return ordered;
	}

	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	public String getSdID() {
		return sdID;
	}

	public void setSdID(String sdID) {
		this.sdID = sdID;
	}

	public FragmentOld getSd() {
		return sd;
	}

	public void setSd(FragmentOld sd) {
		this.sd = sd;
	}

	public void print() {
		System.out.print("Type: " + this.type + "; Name: " + this.name);
		if (this.sd != null) {
			System.out.println("; SD: " + this.sd.getName());
		} else {
			System.out.println();
		}
	}
}
