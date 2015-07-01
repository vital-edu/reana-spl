package Modeling.SequenceDiagrams;

import java.util.ArrayList;

import Modeling.Node;
import Modeling.UnsupportedFragmentTypeException;

public class Fragment extends Node {
	
	// Atributos
		private String name;
		private FragmentType type;
		private ArrayList<Node> nodes;
		private ArrayList<Lifeline> lifelines;
	
	// Construtores
		public Fragment(String id, FragmentType type) {
			super(id);
			name = "";
			this.type = type;
			nodes = new ArrayList<Node>();
			lifelines = new ArrayList<Lifeline>();
		}
		
	// Métodos relevantes
		public void setType(String typeName) throws UnsupportedFragmentTypeException{
			if (typeName.equals("opt")) {
				this.setType(FragmentType.optional);
			} else if (typeName.equals("alt")) {
				this.setType(FragmentType.alternative);
			}else if (typeName.equals("loop")) {
				this.setType(FragmentType.loop);
			} else {
				throw new UnsupportedFragmentTypeException("Fragment of type " + typeName + " is not supported!");
			}
		}
		
		public void print() {
			super.print();
		}
		
	// Getters e Setters
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public FragmentType getType() {
			return type;
		}

		public void setType(FragmentType type) {
			this.type = type;
		}

		public ArrayList<Node> getNodes() {
			return nodes;
		}

		public void setNodes(ArrayList<Node> nodes) {
			this.nodes = nodes;
		}

		public ArrayList<Lifeline> getLifelines() {
			return lifelines;
		}

		public void setLifelines(ArrayList<Lifeline> lifelines) {
			this.lifelines = lifelines;
		}
}
