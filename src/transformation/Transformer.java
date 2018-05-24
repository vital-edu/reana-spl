package transformation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import parsing.Node;
import parsing.activitydiagrams.ADReader;
import parsing.activitydiagrams.Activity;
import parsing.activitydiagrams.ActivityType;
import parsing.activitydiagrams.Edge;
import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.sequencediagrams.Fragment;
import parsing.sequencediagrams.Message;
import parsing.sequencediagrams.MessageType;
import parsing.sequencediagrams.Operand;
import tool.RDGNode;
import fdtmc.FDTMC;
import fdtmc.State;

public class Transformer {
    private static final Logger LOGGER = Logger.getLogger(Transformer.class.getName());
	// Attributes

	private Map<String, FDTMC> fdtmcByName;
	private Map<String, Integer> nCallsByName;
	private Map<String, State> stateByActID;
	private int parNum;
	private int loopNum;

	// Constructors

	public Transformer () {
		fdtmcByName = new HashMap<String, FDTMC>();
		nCallsByName = new HashMap<String, Integer>();
	}

	// Relevant public methods

	/**
	 * Transforms an AD to a fDTMC
	 * @param adParser
	 */
	public RDGNode transformSingleAD(ADReader adParser) {
		FDTMC fdtmc = new FDTMC();

		fdtmc.setVariableName("s" + adParser.getName());
		fdtmcByName.put(adParser.getName(), fdtmc);
		nCallsByName.put(adParser.getName(), 1);

		stateByActID = new HashMap<String, State>();
		State init = fdtmc.createInitialState();
        State error = fdtmc.createErrorState();

		transformPath(fdtmc, init, error, adParser.getActivities().get(0).getOutgoing().get(0));
		LOGGER.finer(fdtmc.toString());

		// The method currently does not support variability in ADs.
		return new RDGNode(adParser.getName(),
		                   "true",
		                   fdtmc);
	}

	/**
	 * Transform an SD to a fDTMC
	 * @param sdParser
	 * @throws InvalidNumberOfOperandsException
	 * @throws InvalidNodeClassException
	 */
	public RDGNode transformSingleSD(Fragment fragment) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		boolean isNew = checkNew (fragment.getName());

		countCallsModel (fragment.getName());

		if (!isNew) { /* Fragmento ja foi modelado */
			return RDGNode.getById(fragment.getName());
		}

		FDTMC fdtmc = new FDTMC();
		State init, error, source;
		parNum = 0;
		loopNum = 0;
		
		final boolean fragmentNameIsNotNull = fragment.getName() != null;
		final boolean fragmentNameIsNotEmpty = !fragment.getName().isEmpty();
		if (fragmentNameIsNotNull && fragmentNameIsNotEmpty) {
			fdtmc.setVariableName("s" + fragment.getName());
			fdtmcByName.put(fragment.getName(), fdtmc);
		} else {
			fdtmc.setVariableName("s" + fragment.getId());
			fdtmcByName.put(fragment.getId(), fdtmc);
		}
		init = fdtmc.createInitialState();
		error = fdtmc.createErrorState();
		source = init;

		RDGNode rdgNode = new RDGNode(fragment.getName(), "true", fdtmc);
		transformFDTMCNodes(fdtmc, fragment.getNodes(), source, error, rdgNode);

		LOGGER.finer(fdtmc.toString());
		return rdgNode;
	}

	public void transformFDTMCNodes(FDTMC fdtmc, List<Node> list, State source, State error, RDGNode currentRDGNode) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		int i = 1;
		State currentSource = source;
		for (Node n : list) {
			if (i++ >= list.size()) {
				State success = fdtmc.createSuccessState();
				if (n.getClass().equals(Message.class)) {
					transformMessage(fdtmc, (Message)n, currentSource, success, error);
				} else if (n.getClass().equals(Fragment.class)) {
					transformFragment(new TransformParameters(fdtmc, (Fragment)n, currentSource, success, error, currentRDGNode));
				}
			} else {
				if (n.getClass().equals(Message.class)) {
				    currentSource = transformMessage(fdtmc, (Message)n, currentSource, fdtmc.createState(), error);
				} else if (n.getClass().equals(Fragment.class)) {
				    currentSource = transformFragment(new TransformParameters(fdtmc, (Fragment)n, currentSource, fdtmc.createState(), error, currentRDGNode));
				}
			}
		}
	}

	public void transformFDTMCNodes(FDTMC fdtmc, List<Node> nodes, State source, State target, State error, RDGNode currentRdgNode) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		int i = 1;
		State currentSource = source;
		for (Node n : nodes) {
			if (i++ >= nodes.size()) {
				if (n.getClass().equals(Message.class)) {
					transformMessage(fdtmc, (Message)n, currentSource, target, error);
				} else if (n.getClass().equals(Fragment.class)) {
					transformFragment(new TransformParameters(fdtmc, (Fragment)n, currentSource, target, error, currentRdgNode));
				}
			} else {
				if (n.getClass().equals(Message.class)) {
				    currentSource = transformMessage(fdtmc, (Message)n, currentSource, fdtmc.createState(), error);
				} else if (n.getClass().equals(Fragment.class)) {
				    currentSource = transformFragment(new TransformParameters(fdtmc, (Fragment)n, currentSource, fdtmc.createState(), error, currentRdgNode));
				}
			}
		}
	}

	// Relevant private methods

	/**
	 * Augments the fDTMC with $msg information
	 * @param fdtmc
	 * @param msg: the message
	 * @param source: the fDTMC node that triggers the message
	 * @param target: the fDTMC node that the message should go to
	 * @param error: the error state where message transmission failure should be transited to
	 * @return the $target itself, the point in the fDTMC where the execution of the message will stop at
	 */
	private State transformMessage(FDTMC fdtmc, Message msg, State source, State target, State error) {
		BigDecimal a = new BigDecimal("1.0");
		BigDecimal b = new BigDecimal(Float.toString(msg.getProb()));

        String msgName = msg.getType().equals(MessageType.ASYNCHRONOUS) ? "" : msg.getName();
        fdtmc.createTransition(source, target, msgName, b.toString());
        if (!a.equals(b)) {
            // PARAM has an issue with alternatives with 0.0 probability, so we simply
            // omit this impossible transition.
            fdtmc.createTransition(source, error, msgName, a.subtract(b).toString());
        }
		return target;
	}

	/**
	 * Distributes the fragment transformation method calls based on the the type of the Fragment
	 * @param transformParameterObject TODO
	 * @throws InvalidNumberOfOperandsException
	 * @throws InvalidNodeClassException
	 */
	private State transformFragment(TransformParameters transformParameterObject) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		switch(transformParameterObject.getFragment().getType()) {
			case LOOP:
				return transformLoopFragment(transformParameterObject);
			case ALTERNATIVE:
				return transformAltFragment(transformParameterObject);
			case OPTIONAL:
				return transformOptFragment(transformParameterObject);
			case PARALLEL:
				return transformParallelFragment(transformParameterObject);
			default:
				break;
		}
		return null;
	}


	/**
	 * Recursively augments the fDTMC with $fragments information
	 * @param transformParameterObject TODO
	 * @param fragment: an fragment of type loop
	 * @param source: the fDTMC node that triggers or not the Fragment
	 * @param target: the fDTMC node that the fragment should return to
	 * @param error: the error state where message transmission failure should be transited to
	 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
	 * @throws InvalidNumberOfOperandsException
	 * @throws InvalidNodeClassException
	 */
	private State transformLoopFragment(TransformParameters transformParameterObject) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		if (transformParameterObject.getFragment().getNodes().size() > 1) {
		    throw new InvalidNumberOfOperandsException("A Loop fragment can only have 1 operand!");
		}

		Operand operand = (Operand)transformParameterObject.getFragment().getNodes().get(0);
		String name = (transformParameterObject.getFragment().getName() != null && !transformParameterObject.getFragment().getName().isEmpty()) ? transformParameterObject.getFragment().getName() : "Loop" + ++loopNum;
		State opStart = transformParameterObject.getFdtmc().createState("initial" + name);
		State opEnd = transformParameterObject.getFdtmc().createState("end" + name);

		// TODO Assuming for now that loop/not-loop probability is 50/50.
		String loopProbability = "0.5";

		transformParameterObject.getFdtmc().createTransition(transformParameterObject.getSource(), transformParameterObject.getTarget(), "", "1 - " + loopProbability); // not entering loop
		transformParameterObject.getFdtmc().createTransition(transformParameterObject.getSource(), opStart, "", loopProbability); // entering loop
		transformParameterObject.getFdtmc().createTransition(opEnd, opStart, "", loopProbability); // restarting loop
		transformParameterObject.getFdtmc().createTransition(opEnd, transformParameterObject.getTarget(), "", "1 - " + loopProbability); // leaving loop

		transformLoopOperand (transformParameterObject.getFdtmc(), name, operand, opStart, opEnd, transformParameterObject.getError(), transformParameterObject.getCurrentRdgNode());
		return transformParameterObject.getTarget();
	}

	/**
	 * Recursively augments the fdtmc with $fragments information
	 * @param transformParameterObject TODO
	 * @param fragment: a fragment of type alternative
	 * @param source: the fDTMC node that triggers or not the Fragment
	 * @param target: the fDTMC node that the fragment should return to
	 * @param error: the error state where message transmission failure should be transited to
	 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
	 * @throws InvalidNodeClassException
	 * @throws InvalidNumberOfOperandsException
	 */
	private State transformAltFragment(TransformParameters transformParameterObject) throws InvalidNodeClassException, InvalidNumberOfOperandsException, InvalidNodeType {
		List<Node> operands = transformParameterObject.getFragment().getNodes();
		String name;

		String opElse = "";
		for(Node node : operands) {
			if (!node.getClass().equals(Operand.class)) {
			    throw new InvalidNodeClassException("An Alt Fragment can only have Operand objects as Nodes!");
			}
			Operand operand = (Operand)node; // to facilitate the nodes use

			String guard = operand.getGuard();
			name = "else".equals(guard) ? transformParameterObject.getFragment().getName() + guard : guard;

			RDGNode altNode = transformOperand (name, guard, operand);
			transformParameterObject.getCurrentRdgNode().addDependency(altNode);
	        // There is a possibility that we have found an RDG node similar to the
	        // one we just transformed. In this case, we reuse the older one.
	        // Thus, the dependency name must be changed accordingly.
	        name = altNode.getId();

			State opStart = transformParameterObject.getFdtmc().createState("initial" + name);
			State opEnd = transformParameterObject.getFdtmc().createState("end" + name);
			State opError = transformParameterObject.getFdtmc().createState("error" + name);

			if (!"else".equals(operand.getGuard())) {
			    // TODO Think about these feature-presence transitions...
			    // They do not adapt to the phi-functions
				opElse = opElse + "f" + guard + " - ";
				transformParameterObject.getFdtmc().createTransition(transformParameterObject.getSource(), opStart, guard, "f" + name); // entering operand
			} else {
				opElse = opElse.substring(0, opElse.length() - 3);
				transformParameterObject.getFdtmc().createTransition(transformParameterObject.getSource(), opStart, guard, "1 - " + opElse);
			}

			transformParameterObject.getFdtmc().createInterface(name, opStart, opEnd, opError);
			transformParameterObject.getFdtmc().createTransition(opEnd, transformParameterObject.getTarget(), "", "1.0"); // leaving operand
		}
		return transformParameterObject.getTarget();
	}


	/**
	 * Recursively augments the fdtmc with $fragments information
	 * @param transformParameterObject TODO
	 * @param fragment: a fragment of type optional
	 * @param source: the fDTMC node that triggers or not the Fragment
	 * @param target: the fDTMC node that the fragment should return to
	 * @param error: the error state where message transmission failure should be transited to
	 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
	 * @throws InvalidNumberOfOperandsException
	 * @throws InvalidNodeClassException
	 */
	private State transformOptFragment(TransformParameters transformParameterObject) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		if (transformParameterObject.getFragment().getNodes().size() > 1) {
		    throw new InvalidNumberOfOperandsException("An Opt fragment can only have 1 operand!");
		}

		Operand operand = (Operand)transformParameterObject.getFragment().getNodes().get(0);
		String name = RDGNode.getNextId();
		String guard = operand.getGuard();

//		creates FDTMC for opt content
		RDGNode optNode = transformOperand(name, guard, operand);
		transformParameterObject.getCurrentRdgNode().addDependency(optNode);
		// There is a possibility that we have found an RDG node similar to the
		// one we just transformed. In this case, we reuse the older one.
		// Thus, the dependency name must be changed accordingly.
		name = optNode.getId();

		State featureStart = transformParameterObject.getFdtmc().createState("initial" + name);
		State featureEnd = transformParameterObject.getFdtmc().createState("end" + name);
		State featureError = transformParameterObject.getFdtmc().createState("error" + name);

        transformParameterObject.getFdtmc().createTransition(transformParameterObject.getSource(), featureStart, name, "1.0"); // into Feature
        // When the feature is not present, its reliability will be taken as 1.
        transformParameterObject.getFdtmc().createInterface(name, featureStart, featureEnd, featureError);
		transformParameterObject.getFdtmc().createTransition(featureEnd, transformParameterObject.getTarget(), "", "1.0"); // leaving Feature

		return transformParameterObject.getTarget();
	}

	/**
	 * Recursively augments the fdtmc with $fragments information
	 * @param transformParameterObject TODO
	 * @param fragment: a fragment of type parallel
	 * @param source: the fDTMC node that triggers or not the Fragment
	 * @param target: the fDTMC node that the fragment should return to
	 * @param error: the error state where message transmission failure should be transited to
	 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
	 */
	private State transformParallelFragment(TransformParameters transformParameterObject) throws InvalidNodeClassException, InvalidNumberOfOperandsException, InvalidNodeType {
		List<Node> operands = transformParameterObject.getFragment().getNodes();
		String fragName, opName;
		int n = operands.size(), opNum;
		float val = 1/(float)n;

		fragName = !transformParameterObject.getFragment().getName().isEmpty() ? transformParameterObject.getFragment().getName() : "Par" + ++parNum;
		opNum = 0;
		for(Node node : operands) {
			if (!node.getClass().equals(Operand.class)) {
			    throw new InvalidNodeClassException("A Par Fragment can only have Operand objects as Nodes!");
			}
			Operand operand = (Operand)node; // to facilitate the nodes use
			opName = fragName + "-Op" + ++opNum;

			RDGNode fragmentNode = transformOperand(opName, "true", operand);
			transformParameterObject.getCurrentRdgNode().addDependency(fragmentNode);
	        // There is a possibility that we have found an RDG node similar to the
	        // one we just transformed. In this case, we reuse the older one.
	        // Thus, the dependency name must be changed accordingly.
	        opName = fragmentNode.getId();

			State opStart = transformParameterObject.getFdtmc().createState("initial" + opName);
			State opEnd = transformParameterObject.getFdtmc().createState("end" + opName);
			State opError = transformParameterObject.getFdtmc().createState("error" + opName);

			transformParameterObject.getFdtmc().createTransition(transformParameterObject.getSource(), opStart, "", Float.toString(val)); // entering operand
			transformParameterObject.getFdtmc().createInterface(opName, opStart, opEnd, opError);
			transformParameterObject.getFdtmc().createTransition(opEnd, transformParameterObject.getTarget(), "", "1.0"); // leaving operand

		}
		return transformParameterObject.getTarget();
	}

	private RDGNode transformOperand (String name, String presenceCondition, Operand operand) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		boolean isNew = checkNew (name);

		countCallsModel (name);

		if (!isNew) { /* Fragmento ja foi modelado */
			return RDGNode.getById(name);
		}

		FDTMC fdtmc = new FDTMC();

		fdtmc.setVariableName("s" + name);
		fdtmcByName.put(name, fdtmc);

		State init = fdtmc.createInitialState();
		State error = fdtmc.createErrorState();
		State source = init;

		RDGNode rdgNode = new RDGNode(name, presenceCondition, fdtmc);
		transformFDTMCNodes(fdtmc, operand.getNodes(), source, error, rdgNode);
		LOGGER.finer(fdtmc.toString());

		RDGNode similarNode = RDGNode.getSimilarNode(rdgNode);
		if (similarNode != null) {
		    return similarNode;
		} else {
		    return rdgNode;
		}
	}

	private void transformLoopOperand (FDTMC fdtmc, String name, Operand operand, State source, State target, State error, RDGNode currentRdgNode) throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {

		transformFDTMCNodes(fdtmc, operand.getNodes(), source, target, error, currentRdgNode);
		LOGGER.finer(fdtmc.toString());
	}

	/**
	 * transformSingleAD auxiliary method
	 * @param fdtmc
	 * @param sourceState
	 * @param adEdge
	 */
	private void transformPath(FDTMC fdtmc, State sourceState, State errorState, Edge adEdge) {
		Activity targetAct = adEdge.getTarget();
		Activity sourceAct = adEdge.getSource();
		State targetState;

		String sourceActivitySD = sourceAct.getSd() != null ? sourceAct.getSd().getName() : "";

		if (sourceAct.getType().equals(ActivityType.INITIAL_NODE)) {
			for (Edge e : targetAct.getOutgoing()) {
				transformPath(fdtmc, sourceState, errorState, e);
			}
		} else if (sourceAct.getType().equals(ActivityType.CALL)) {
			stateByActID.put(sourceAct.getId(), sourceState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap

			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.FINAL_NODE)) {
					targetState = fdtmc.createSuccessState();
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();

                fdtmc.createInterface(sourceActivitySD, sourceState, targetState, errorState);

				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, errorState, e);
				}
			} else { // atividade target ja foi criada
			    fdtmc.createInterface(sourceActivitySD, sourceState, targetState, errorState);
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.DECISION)) {
			stateByActID.put(sourceAct.getId(), sourceState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap

			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.FINAL_NODE)) {
					targetState = fdtmc.createSuccessState();
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();

				fdtmc.createTransition(sourceState, targetState, "", Float.toString(adEdge.getProbability()));

				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, errorState, e);
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(sourceState, targetState, "", Float.toString(adEdge.getProbability()));
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.MERGE)) {
			stateByActID.put(sourceAct.getId(), sourceState); // insere source no hashmap
			targetState = stateByActID.get(targetAct.getId()); // verifica se target esta no hashmap

			if (targetState == null) { // atividade target nao foi criada
				if (targetAct.getType().equals(ActivityType.FINAL_NODE)) {
					targetState = fdtmc.createSuccessState();
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
				else targetState = fdtmc.createState();

				fdtmc.createTransition(sourceState, targetState, sourceAct.getName(), "1.0");

				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, errorState, e);
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(sourceState, targetState, sourceAct.getName(), "1.0");
				/* end path */
			}
		}
	}

	// Effort measurement methods

	public boolean checkNew (String name) {
		if (fdtmcByName.get(name) != null) {
			return false;
		}
		return true;
	}

	public void countCallsModel (String name) {
		if (fdtmcByName.get(name) != null) {
			nCallsByName.put(name, nCallsByName.get(name) + 1);
			return;
		}
		nCallsByName.put(name, 1);
	}

	public void measureSizeModel (FDTMC fdtmc) {
		Integer nStates, nTrans = 0;

		nStates = fdtmc.getStates().size();
		Set<State> states = fdtmc.getTransitions().keySet();
		Iterator <State> itStates = states.iterator();
		while (itStates.hasNext()) {
			State temp = itStates.next();
			if (fdtmc.getTransitions().get(temp) != null)
				nTrans += fdtmc.getTransitions().get(temp).size();
		}
		LOGGER.finer("Model Size: " + nStates + " states; " + nTrans + " transitions.");
	}

	public void printNumberOfCalls (String name) {
		int num = nCallsByName.get(name);
		LOGGER.finer(Integer.toString(num));
	}

	// Getters and Setters

	public Map<String, FDTMC> getFdtmcByName() {
		return fdtmcByName;
	}

	public Map<String, Integer> getnCallsByName() {
		return nCallsByName;
	}
}
