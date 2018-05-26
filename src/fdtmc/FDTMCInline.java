package fdtmc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FDTMCInline { 

	public FDTMCInline() {
	}
	
    public FDTMC inline(Map<String, FDTMC> indexedModels, Map<State, State> statesMapping, FDTMC inlined, FDTMC origin) {

        for (Map.Entry<String, List<Interface>> entry: origin.getInterfaces().entrySet()) {
            String dependencyId = entry.getKey();
            if (indexedModels.containsKey(dependencyId)) {
                FDTMC fragment = indexedModels.get(dependencyId);
                for (Interface iface: entry.getValue()) {
                    inlineInInterface(iface, fragment, statesMapping, origin);
                }
            }
        }
        return inlined;
    }
	
	protected Map<State, State> inlineStates(FDTMC fdtmc, FDTMC origin) {
        Map<State, State> statesOldToNew = new HashMap<State, State>();
        for (State state: fdtmc.getStates()) {
            State newState = origin.createState();
            statesOldToNew.put(state, newState);
        }
        return statesOldToNew;
    }
	
	protected void inlineTransitions(FDTMC fdtmc, Map<State, State> statesOldToNew, FDTMC origin) {
        Set<Transition> interfaceTransitions = fdtmc.getInterfaceTransitions();
        for (Map.Entry<State, List<Transition>> entry : fdtmc.getTransitions().entrySet()) {
            List<Transition> transitions = entry.getValue();
            if (transitions != null) {
                for (Transition transition : transitions) {
                    if (!interfaceTransitions.contains(transition)) {
                        inlineTransition(transition, statesOldToNew, origin);
                    }
                }
            }
        }
    }
	
	protected Transition inlineTransition(Transition transition, Map<State, State> statesOldToNew, FDTMC origin) {
        State newSource = statesOldToNew.get(transition.getSource());
        State newTarget = statesOldToNew.get(transition.getTarget());
        return origin.createTransition(
        		newSource,
        		newTarget,
        		transition.getActionName(),
        		transition.getProbability()
        );
    }
	
	protected void inlineInterfaces(FDTMC fdtmc, Map<State, State> statesOldToNew, FDTMC origin) {
        for (Map.Entry<String, List<Interface>> entry : fdtmc.getInterfaces().entrySet()) {
            List<Interface> newInterfaces = new LinkedList<Interface>();
            origin.getInterfaces().put(entry.getKey(), newInterfaces);
            for (Interface iface : entry.getValue()) {
                Transition successTransition = inlineTransition(iface.getSuccessTransition(), statesOldToNew, origin);
                Transition errorTransition = inlineTransition(iface.getErrorTransition(), statesOldToNew, origin);
                Interface newInterface = new Interface(
                		iface.getAbstractedId(),
                		statesOldToNew.get(iface.getInitial()),
                		statesOldToNew.get(iface.getSuccess()),
                		statesOldToNew.get(iface.getError()),
                		successTransition,
                		errorTransition
                );
                newInterfaces.add(newInterface);
            }
        }
    }
	
	
    protected void inlineInInterface(Interface iface, FDTMC fragment, Map<State, State> statesMapping, FDTMC origin) {
        Map<State, State> fragmentStatesMapping = origin.inlineStates(fragment);
        origin.inlineTransitions(fragment, fragmentStatesMapping);

        origin.createTransition(
        		statesMapping.get(iface.getInitial()),
        		fragmentStatesMapping.get(fragment.getInitialState()),
        		"",
        		"1"
        );
        origin.createTransition(
        		fragmentStatesMapping.get(fragment.getSuccessState()),
        		statesMapping.get(iface.getSuccess()),
        		"",
        		"1"
        );
        if (fragment.getErrorState() != null) {
        	origin.createTransition(
        			fragmentStatesMapping.get(fragment.getErrorState()),
        			statesMapping.get(iface.getError()),
        			"",
        			"1"
        	);
        }
    }
}