package FeatureFamilyBasedAnalysisTool;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FDTMC {

	private HashSet<State> states;
	private State initialState;
	private HashSet<String> labels;
	private String variableName;
	private int index;
	private HashMap<State, List<Transition>> transitionSystem;


	public FDTMC() {
		states = new HashSet<State>();
		initialState = null;
		labels = new HashSet<String>();
		variableName = null;
		index = 0;
		transitionSystem = new HashMap<State, List<Transition>>();
	}

	public Collection<State> getStates() {
		return states;
	}

	public State getInitialState() {
		return initialState;
	}

	public Collection<String> getLabels() {
		return labels;
	}

	public void setVariableName(String name) {
		variableName = name;
	}

	public String getVariableName() {
		return variableName;
	}

	public int getVariableIndex() {
		return index;
	}

	public State createState() {
		State temp = new State();
		temp.setVariableName(variableName);
		temp.setIndex(index);
		states.add(temp);
		index++;
		return temp;
	}

	public State createState(String label) {
		State temp = createState();
		temp.setLabel(label);
		return temp;
	}

	public boolean createTransition(State source, State target, String reliability) {
		List<Transition> l = transitionSystem.get(source);
//		System.out.println("L e nulo ou nao? " + l);
		if (l == null) {
			l = new LinkedList<Transition>();
		}
//		System.out.println("Valor de L " + l.toString());
		boolean answer = l.add(new Transition(source, target, reliability));
//		System.out.println("Valor de L " + l.toString());
//		System.out.println("FDTMC::createTransition --> vou adicionar transicao a lista de adjacencia de " + source.getLabel());
		transitionSystem.put(source, l);
//		System.out.println(transitionSystem.toString());

//		System.out.println("FDTMC::createTransition --> answer = " + answer);
		return answer;
	}

	public State getStateByLabel(String label) {
		Iterator <State> it = states.iterator();
		while (it.hasNext()){
			State s = it.next();
			if (s.getLabel().equals(label))
				return s;
		}
		return null;
	}

	@Override
	public String toString() {
		String msg = new String();
		msg += "****************************************************************" + "\n";
		msg += "FDTMC " + "\n";
		msg += "****************************************************************" + "\n";
		msg += "\n";
		msg += "Variable name: " + this.variableName + "\n";
		msg += "FDTMC size: " + this.states.size() + " states and " + this.transitionSystem.size() + " transitions." + "\n";
		msg += "\n";
		msg += "Transitions:" + "\n";

		Set<State> states = this.transitionSystem.keySet();
		Iterator <State> itStates = states.iterator();
		while (itStates.hasNext()) {
			State temp = itStates.next();
			List<Transition> transitionList = this.transitionSystem.get(temp);
			Iterator <Transition> itTransitions = transitionList.iterator();
			while (itTransitions.hasNext()) {
				Transition t = itTransitions.next();
				msg += temp.getVariableName() + "=" + temp.getIndex() + ((temp.getLabel() != null) ? "(" + temp.getLabel() + ")" : "") +
						" --- " + t.getProbability() +
						" ---> " + t.getTarget().getVariableName() + "=" + t.getTarget().getIndex() + ((t.getTarget().getLabel() != null) ? "(" + t.getTarget().getLabel() + ")" : "") + "\n";
			}
		}
		return msg;
	}

	public Map<State, List<Transition>> getTransitions() {
		return transitionSystem;
	}
}
