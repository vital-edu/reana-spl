package fdtmc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FDTMC {

	private LinkedHashSet<State> states;
	private State initialState;
	private HashSet<String> labels;
	private String variableName;
	private int index;
	private LinkedHashMap<State, LinkedList<Transition>> transitionSystem;
	private HashMap<Feature, FDTMC> interfaces; 
	
	
	public FDTMC() {
		states = new LinkedHashSet<State>();
		initialState = null; 
		labels = new HashSet<String>();
		variableName = null;
		index = 0;
		transitionSystem = new LinkedHashMap<State, LinkedList<Transition>>();
		interfaces = new HashMap<Feature, FDTMC>();
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
		LinkedList<Transition> l = transitionSystem.get(source);

		if (l == null) {
			l = new LinkedList<Transition>();
		}

		boolean answer = l.add(new Transition(source, target, action, reliability));
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

	public Transition getTransitionByActionName(String action) {
		//para cada Lista de adjacencias de cada nodo
		Collection<LinkedList<Transition>> stateAdjacencies = transitionSystem.values();
		Iterator <LinkedList<Transition>> iteratorStateAdjacencies = stateAdjacencies.iterator();
		while (iteratorStateAdjacencies.hasNext()) {
			LinkedList<Transition> transitions = iteratorStateAdjacencies.next();

			//Percorrer a lista de transicoes e comparar os labels das transicoes
			Iterator <Transition> iteratorTransitions = transitions.iterator(); 
			while (iteratorTransitions.hasNext()) {
				Transition t = iteratorTransitions.next();
//				System.out.println("\tAction Name: " + t.getActionName() );
				if (t.getActionName().equals(action))
					return t;
			}
		}
		return null;		
	}
	
	
	@Override
	public String toString() {
		String msg = new String();
//		msg += "****************************************************************" + "\n";
//		msg += "FDTMC " + "\n";
//		msg += "****************************************************************" + "\n";
//		msg += "\n";
//		msg += "Variable name: " + this.variableName + "\n";
//		msg += "FDTMC size: " + this.states.size() + " states and " + this.transitionSystem.size() + " transitions." + "\n";
//		msg += "\n";
//		msg += "Transitions:" + "\n";

		Set<State> states = this.transitionSystem.keySet();
		Iterator <State> itStates = states.iterator();
		while (itStates.hasNext()) {
			State temp = itStates.next(); 
			LinkedList<Transition> transitionList = this.transitionSystem.get(temp);
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
		String msg = new String(); 
		
		Set<State> states =  transitionSystem.keySet();
		System.out.println("states " + states);
		Iterator<State> it = states.iterator();
		//Imprimir chaves em ordem
		while (it.hasNext()) {
			State s = it.next();
			System.out.println(s.getVariableName() + s.getIndex());
		}
		return ""; 
	}

	public HashMap<Feature, FDTMC> getInterfaces() {
		return interfaces;
	}

	public Map<State, LinkedList<Transition>> getTransitions() {
		return transitionSystem;
	}
}
