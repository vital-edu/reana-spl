package Modeling.SequenceDiagrams;

import java.util.ArrayList;
import java.util.HashMap;

import Modeling.Node;

public class Operand extends Node {
	// Atributos
		private String guard;
		private ArrayList<Node> nodes;
		private HashMap<String, Node> nodeById;
	
	// Construtores
		public Operand(String id) {
			super(id);
			nodes = new ArrayList<Node>();
			nodeById = new HashMap<String, Node>();
		}

	// Métodos relevantes
		public void addNode(Node node) {
			this.nodes.add(node);
			this.nodeById.put(node.getId(), node);
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
}
