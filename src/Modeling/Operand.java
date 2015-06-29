package Modeling;

import java.util.ArrayList;
import java.util.HashMap;

public class Operand extends Node {
	// Atributos
		private String guard;
		private ArrayList<Node> nodes;
		private HashMap<String, Node> nodeByID;
	
	public Operand(String id) {
		super(id);
		setNodes(new ArrayList<Node>());
		setNodeByID(new HashMap<String, Node>());
	}

	// Getters e Setters
		public String getGuard() {
			return guard;
		}
		
		public void setGuard(String guard) {
			this.guard = guard;
		}
		
		public ArrayList<Node> getNodes() {
			return nodes;
		}
		
		public void setNodes(ArrayList<Node> nodes) {
			this.nodes = nodes;
		}
		
		public HashMap<String, Node> getNodeByID() {
			return nodeByID;
		}
		
		public void setNodeByID(HashMap<String, Node> nodeByID) {
			this.nodeByID = nodeByID;
		}
		
	// Demais métodos
		public void addNode(Node node) {
			nodes.add(node);
			this.nodeByID.put(node.getId(), node);
		}
}
