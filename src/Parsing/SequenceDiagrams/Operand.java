package Parsing.SequenceDiagrams;

import java.util.ArrayList;
import java.util.HashMap;

import Parsing.Node;

public class Operand extends Node {
	// Attributes
	
		private String guard;
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
		
		public String getGuard() {
			return guard;
		}
		
		public void setGuard(String guard) {
			this.guard = guard;
		}

		public ArrayList<Node> getNodes() {
			return nodes;
		}
}
