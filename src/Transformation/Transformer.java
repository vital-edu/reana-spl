package Transformation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.State;
import Parsing.ADReader;
import Parsing.Activity;
import Parsing.ActivityType;
import Parsing.Edge;
import Parsing.Fragment;
import Parsing.Message;
import Parsing.MessageType;
import Parsing.Node;

public class Transformer {
	private HashMap<String, FDTMC> fdtmcByName;
	private HashMap<String, Integer> nCallsByName;
	private HashMap<String, State> stateByActID;
	
	public Transformer () {
		fdtmcByName = new HashMap<String, FDTMC>();
		nCallsByName = new HashMap<String, Integer>();
	}
	
	public HashMap<String, FDTMC> getFdtmcByName() {
		return fdtmcByName;
	}

	public HashMap<String, Integer> getnCallsByName() {
		return nCallsByName;
	}
	
	public void transformSingleAD(ADReader adParser) {
		FDTMC fdtmc = new FDTMC();
		State init;
		
		fdtmc.setVariableName("s" + adParser.getName());
		fdtmcByName.put(adParser.getName(), fdtmc);
		nCallsByName.put(adParser.getName(), 1);

		stateByActID = new HashMap<String, State>();
		init = fdtmc.createState("initial");

		transformPath(fdtmc, init, adParser.getActivities().get(0).getOutgoing().get(0));
		System.out.println(fdtmc.toString());
	}

	public void transformPath(FDTMC fdtmc, State fdtmcState, Edge adEdge) {
		Activity targetAct = adEdge.getTarget();
		Activity sourceAct = adEdge.getSource();
		State targetState;

		if (sourceAct.getType().equals(ActivityType.initialNode)) {
			for (Edge e : targetAct.getOutgoing()) {
				transformPath(fdtmc, fdtmcState, e);
			}
		} else if (sourceAct.getType().equals(ActivityType.call)) {
			stateByActID.put(sourceAct.getId(), fdtmcState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap
			
			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					targetState = fdtmc.createState("final");
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();
				
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "r"
						+ sourceAct.getName());
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "r"
						+ sourceAct.getName());
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.decision)) {
			stateByActID.put(sourceAct.getId(), fdtmcState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap
			
			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					targetState = fdtmc.createState("final");
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();
				
				fdtmc.createTransition(fdtmcState, targetState, "", adEdge.getGuard());
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(fdtmcState, targetState, "", adEdge.getGuard());
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.merge)) {
			stateByActID.put(sourceAct.getId(), fdtmcState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap
			
			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					targetState = fdtmc.createState("final");
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();
				
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "1.0");
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "1.0");
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.fork)) {
			stateByActID.put(sourceAct.getId(), fdtmcState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap
			
			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					targetState = fdtmc.createState("final");
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();
				
				int n = sourceAct.getOutgoing().size();
				fdtmc.createTransition(fdtmcState, targetState, "", Float.toString(1.0f/n));
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
			} else { // atividade target ja foi criada
				int n = sourceAct.getOutgoing().size();
				fdtmc.createTransition(fdtmcState, targetState, "", Float.toString(1.0f/n));
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.join)) {
			stateByActID.put(sourceAct.getId(), fdtmcState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap
			
			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					targetState = fdtmc.createState("final");
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();
				
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "1.0");
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "1.0");
				/* end path */
			}
		}
	}

	public void transformSingleSD(Fragment fragment) {
		boolean isNew = checkNew (fragment);
		
		if (!isNew) { /* Fragmento ja foi modelado */
			countCallsModel (fragment);
			return;
		}
		
		countCallsModel (fragment);
		
		FDTMC fdtmc = new FDTMC();
		State init, error, success, source, target, featStart;

		/* Cria var estado / Insere no HashMap com nome SD ou nome Feature */
		if (fragment.getOperandName() != null) {
			fdtmc.setVariableName("s" + fragment.getOperandName());
			fdtmcByName.put(fragment.getOperandName(), fdtmc);
		} else {
			fdtmc.setVariableName("s" + fragment.getName());
			fdtmcByName.put(fragment.getName(), fdtmc);
		}

		init = fdtmc.createState("init");
		error = fdtmc.createState("error");
		source = init;

		int i = 1;
		for (Node n : fragment.getNodes()) {
			if (i++ == fragment.getNodes().size()) {
				success = fdtmc.createState("success");
				if (n.getClass().equals(Message.class)) {
					BigDecimal a = new BigDecimal("1.0");
					BigDecimal b = new BigDecimal(Float.toString(n.getProb()));
					if (((Message) n).getType().equals(MessageType.asynchronous)) {
						fdtmc.createTransition(source, success, "", b.toString());
						fdtmc.createTransition(source, error, "", a.subtract(b).toString());
					} else { /* Mensagem sincrona */
						fdtmc.createTransition(source, success, ((Message) n).getName(), b.toString());
						fdtmc.createTransition(source, error, ((Message) n).getName(), a.subtract(b).toString());
					}
				} else if (n.getClass().equals(Fragment.class)) {
					String featureName = ((Fragment) n).getOperandName();
					featStart = fdtmc.createState("init" + featureName);
					fdtmc.createTransition(source, featStart, featureName, "f" + featureName);
					fdtmc.createTransition(source, success, featureName, "1-f" + featureName);
					/* Interface Begin */
					fdtmc.createTransition(featStart, fdtmc.createState("end" + featureName), "",
							"");
					fdtmc.createTransition(featStart, fdtmc.createState("error" + featureName), "",
							"");
					/* Interface End */
					transformSingleSD((Fragment) n);
				}
			} else {
				if (n.getClass().equals(Message.class)) {
					target = fdtmc.createState();
					BigDecimal a = new BigDecimal("1.0");
					BigDecimal b = new BigDecimal(Float.toString(n.getProb()));
					if (((Message) n).getType().equals(MessageType.asynchronous)) {
						fdtmc.createTransition(source, target, "", b.toString());
						fdtmc.createTransition(source, error, "", a.subtract(b).toString());
					} else { /* Mensagem sincrona */
						fdtmc.createTransition(source, target, ((Message) n).getName(), b.toString());
						fdtmc.createTransition(source, error, ((Message) n).getName(), a.subtract(b).toString());
					}
					source = target;
				} else if (n.getClass().equals(Fragment.class)) {
					String featureName = ((Fragment) n).getOperandName();
					featStart = fdtmc.createState("init" + featureName);
					target = fdtmc.createState();
					fdtmc.createTransition(source, featStart, featureName, "f" + featureName);
					fdtmc.createTransition(source, target, featureName, "1-f" + featureName);
					/* Interface Begin */
					fdtmc.createTransition(featStart, fdtmc.createState("end" + featureName), "",
							"");
					fdtmc.createTransition(featStart, fdtmc.createState("error" + featureName), "",
							"");
					/* Interface End */
					transformSingleSD((Fragment) n);
					source = target;
				}
			}
		}
		System.out.println(fdtmc.toString());
		//measureSizeModel (fdtmc);
	}
	
	public boolean checkNew (Fragment fragment) {
		if (fragment.getOperandName() != null) {
			if (fdtmcByName.get(fragment.getOperandName()) != null) {
				return false;
			}
			return true;
		} else {
			if (fdtmcByName.get(fragment.getName()) != null) {
				return false;
			}
			return true;
		}
	}
	
	public void countCallsModel (Fragment fragment) {
		
		if (fragment.getOperandName() != null) {
			if (fdtmcByName.get(fragment.getOperandName()) != null) {
				nCallsByName.put(fragment.getOperandName(), nCallsByName.get(fragment.getOperandName()) + 1);
				return;
			}
			nCallsByName.put(fragment.getOperandName(), 1);
		} else {
			if (fdtmcByName.get(fragment.getName()) != null) {
				nCallsByName.put(fragment.getName(), nCallsByName.get(fragment.getName()) + 1);
				return;
			}
			nCallsByName.put(fragment.getName(), 1);
		}
	}
	
	public void measureSizeModel (FDTMC fdtmc) {
		Integer nStates, nTrans = 0;
		
		nStates = fdtmc.getStates().size();
		Set<State> states = fdtmc.getTransitions().keySet();
		Iterator <State> itStates = states.iterator();
		while (itStates.hasNext()) {
			State temp = itStates.next();
			nTrans += fdtmc.getTransitions().get(temp).size();
		}
		System.out.println("Model Size: " + nStates + " states; " + nTrans + " transitions.");
	}
	
	public void printNumberOfCalls (String name) {
		int num = nCallsByName.get(name);
		System.out.println(num);
	}
}
