package fdtmc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FDTMC {

	private Set<State> states;
	private State initialState;
	private Set<String> labels;
	private String variableName;
	private int index;
	private Map<State, List<Transition>> transitionSystem;


	public FDTMC() {
		states = new LinkedHashSet<State>();
		initialState = null;
		labels = new HashSet<String>();
		variableName = null;
		index = 0;
		transitionSystem = new LinkedHashMap<State, List<Transition>>();
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
		transitionSystem.put(temp, null);
		if (index == 0)
			initialState = temp;
		index++;
		return temp;
	}

	public State createState(String label) {
		State temp = createState();
		temp.setLabel(label);
		return temp;
	}

	public boolean createTransition(State source, State target, String action, String reliability) {
		List<Transition> l = transitionSystem.get(source);

		if (l == null) {
			l = new LinkedList<Transition>();
		}

		boolean answer = l.add(new Transition(source, target, action, reliability));
		transitionSystem.put(source, l);
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

	public Transition getTransitionByActionName(String action) {
		//para cada Lista de adjacencias de cada nodo
		Collection<List<Transition>> stateAdjacencies = transitionSystem.values();
		Iterator<List<Transition>> iteratorStateAdjacencies = stateAdjacencies.iterator();
		while (iteratorStateAdjacencies.hasNext()) {
			List<Transition> transitions = iteratorStateAdjacencies.next();

			//Percorrer a lista de transicoes e comparar os labels das transicoes
			Iterator <Transition> iteratorTransitions = transitions.iterator();
			while (iteratorTransitions.hasNext()) {
				Transition t = iteratorTransitions.next();
				if (t.getActionName().equals(action))
					return t;
			}
		}
		return null;
	}


	@Override
	public String toString() {
		String msg = new String();

		Set<State> tmpStates = this.transitionSystem.keySet();
		Iterator <State> itStates = tmpStates.iterator();
		while (itStates.hasNext()) {
			State temp = itStates.next();
			List<Transition> transitionList = this.transitionSystem.get(temp);
			if (transitionList != null) {
				Iterator <Transition> itTransitions = transitionList.iterator();
				while (itTransitions.hasNext()) {
					Transition t = itTransitions.next();
					msg += temp.getVariableName() + "=" + temp.getIndex() + ((temp.getLabel() != null) ? "(" + temp.getLabel() + ")" : "") +
							" --- " + t.getActionName() + " / " + t.getProbability() +
							" ---> " + t.getTarget().getVariableName() + "=" + t.getTarget().getIndex() + ((t.getTarget().getLabel() != null) ? "(" + t.getTarget().getLabel() + ")" : "") + "\n";
				}
			}
		}
		return msg;
	}

	public String toString2() {
	    String result = "";
		Set<State> tmpStates =  transitionSystem.keySet();
		result += "states " + tmpStates + "\n";
		Iterator<State> it = tmpStates.iterator();
		//Imprimir chaves em ordem
		while (it.hasNext()) {
			State s = it.next();
			result += s.getVariableName() + s.getIndex() + "\n";
		}
		return result;
	}

	public Map<State, List<Transition>> getTransitions() {
		return transitionSystem;
	}
}
