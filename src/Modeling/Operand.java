package Modeling;

import java.util.ArrayList;
import java.util.HashMap;

public class Operand extends Node {
	// Atributos
		private String guard;
		private HashMap<String, Node> nodes;
	
	// Construtores
		public Operand(String id) {
			super(id);
			setNodes(new HashMap<String, Node>());
		}

	// Getters e Setters
		public String getGuard() {
			return guard;
		}
		
		public void setGuard(String guard) {
			this.guard = guard;
		}

		
		public HashMap<String, Node> getNodes() {
			return nodes;
		}

		public void setNodes(HashMap<String, Node> nodes) {
			this.nodes = nodes;
		}

	// Demais métodos
		public void addNode(Node node) {
			this.nodes.put(node.getId(), node);
		}
}
