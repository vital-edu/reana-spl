package Modeling.SequenceDiagrams;

import java.util.ArrayList;
import java.util.HashMap;

import Modeling.Node;

public class Operand extends Node {
	// Attributes
	
		private Float guard;
		private ArrayList<Node> nodes;
		private HashMap<String, Node> nodeById;
	
	// Constructors
		
		public Operand(String id) {
			super(id);
			nodes = new ArrayList<Node>();
			nodeById = new HashMap<String, Node>();
		}

	// Relevant Methods
		
		public void addNode(Node node) {
			this.nodes.add(node);
			this.nodeById.put(node.getId(), node);
		}

	// Getters and Setters
		
		public Float getGuard() {
			return guard;
		}
		
		public void setGuard(Float guard) {
			this.guard = guard;
		}

		public ArrayList<Node> getNodes() {
			return nodes;
		}
}
