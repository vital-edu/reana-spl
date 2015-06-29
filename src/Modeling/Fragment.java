package Modeling;

import java.util.ArrayList;

public class Fragment extends Node {
	
	// Atributos
		private String name;
		private FragmentType type;
		private ArrayList<Operand> operands;
		private ArrayList<Lifeline> lifelines;
	
	// Construtores
		public Fragment(String id, FragmentType type) {
			super(id);
			name = "";
			this.type = type;
			operands = new ArrayList<Operand>();
			lifelines = new ArrayList<Lifeline>();
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

		public ArrayList<Operand> getOperands() {
			return operands;
		}

		public void setOperands(ArrayList<Operand> operands) {
			this.operands = operands;
		}

		public ArrayList<Lifeline> getLifelines() {
			return lifelines;
		}

		public void setLifelines(ArrayList<Lifeline> lifelines) {
			this.lifelines = lifelines;
		}
	
	// Demais métodos
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
}
