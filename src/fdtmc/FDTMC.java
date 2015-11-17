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

    public static final String INITIAL_LABEL = "initial";
    public static final String SUCCESS_LABEL = "success";
    public static final String ERROR_LABEL = "error";

	private Set<State> states;
    private State initialState;
    private State successState;
    private State errorState;
	private String variableName;
	private int index;
	private Map<State, List<Transition>> transitionSystem;
	private Map<String, List<Interface>> interfaces;


	public FDTMC() {
		states = new LinkedHashSet<State>();
		initialState = null;
		variableName = null;
		index = 0;
		transitionSystem = new LinkedHashMap<State, List<Transition>>();
		interfaces = new LinkedHashMap<String, List<Interface>>();
	}

	public Collection<State> getStates() {
		return states;
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

    public State createInitialState() {
        State initial = createState();
        setInitialState(initial);
        return initial;
    }

    private void setInitialState(State initialState) {
        this.initialState = initialState;
        initialState.setLabel(INITIAL_LABEL);
    }

    public State getInitialState() {
        return initialState;
    }

    public State createSuccessState() {
        State success = createState();
        setSuccessState(success);
        return success;
    }

    private void setSuccessState(State successState) {
        this.successState = successState;
        successState.setLabel(SUCCESS_LABEL);
    }

    public State getSuccessState() {
        return successState;
    }

    public State createErrorState() {
        State error = createState();
        setErrorState(error);
        return error;
    }

    private void setErrorState(State errorState) {
        this.errorState = errorState;
        errorState.setLabel(ERROR_LABEL);
    }

    public State getErrorState() {
        return errorState;
    }

	public Transition createTransition(State source, State target, String action, String reliability) {
	    if (source == null) {
	        return null;
	    }

	    List<Transition> l = transitionSystem.get(source);
		if (l == null) {
			l = new LinkedList<Transition>();
		}

		Transition newTransition = new Transition(source, target, action, reliability);
		boolean success = l.add(newTransition);
		transitionSystem.put(source, l);
		return success ? newTransition : null;
	}

	/**
	 * Creates an explicit interface to another FDTMC.
	 *
	 * An interface is an FDTMC fragment with 3 states (initial, success, and error)
	 * and 2 transitions (initial to success with probability {@code id} and initial
	 * to error with probability 1 - {@code id}).
	 *
	 * @param id Identifier of the FDTMC to be abstracted away.
	 * @param initial Initial state of the interface.
	 * @param success Success state of the interface.
	 * @param error Error state of the interface.
	 */
	public Interface createInterface(String id, State initial, State success, State error) {
	    Transition successTransition = createTransition(initial, success, "", id);
	    Transition errorTransition = createTransition(initial, error, "", "1 - " + id);
	    Interface newInterface = new Interface(initial,
	                                           success,
	                                           error,
	                                           successTransition,
	                                           errorTransition);

	    List<Interface> interfaceOccurrences = null;
	    if (interfaces.containsKey(id)) {
	        interfaceOccurrences = interfaces.get(id);
	    } else {
	        interfaceOccurrences = new LinkedList<Interface>();
	        interfaces.put(id, interfaceOccurrences);
	    }
	    interfaceOccurrences.add(newInterface);
	    return newInterface;
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
