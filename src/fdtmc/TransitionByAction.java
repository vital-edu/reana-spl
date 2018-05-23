package fdtmc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransitionByAction {
	private String action;
	private Collection<List<Transition>> stateAdjacencies;
	private Iterator<List<Transition>> iteratorStateAdjacencies;
	private List<Transition> transitions;
	private Iterator <Transition> iteratorTransitions;
	private Transition t;
	
	private FDTMC fdtmc;

	public TransitionByAction(String action, FDTMC fdtmc) {
		super();
		this.action = action;
		this.fdtmc = fdtmc;
	}
	
	public Transition compute(){
		// Para cada Lista de adjacencias de cada nodo
		stateAdjacencies = fdtmc.getTransitionSystem().values();
		iteratorStateAdjacencies = stateAdjacencies.iterator();
		
		while (iteratorStateAdjacencies.hasNext()) {
			transitions = iteratorStateAdjacencies.next();

			return searchAction();
		}
		return null;
	}

	private Transition searchAction() {
		iteratorTransitions = transitions.iterator();
		
		while (iteratorTransitions.hasNext()) {
			t = iteratorTransitions.next();
				if (t.getActionName().equals(action))
					return t;
		}

		return null;
	}
	
}