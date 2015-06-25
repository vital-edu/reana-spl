package Modeling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.State;

public class DiagramAPI {
	private final File xmlFile;
	private ArrayList<SDReader> sdParsers;
	private ArrayList<ADReader> adParsers;
	// private ArrayList<FDTMC> fdtmcs;
	private HashMap<String, Fragment> sdByID;
	private HashMap<String, FDTMC> fdtmcByName;
	private HashMap<String, State> stateByActID;

	public DiagramAPI(File xmlFile) {
		this.xmlFile = xmlFile;
		adParsers = new ArrayList<ADReader>();
		sdParsers = new ArrayList<SDReader>();
		sdByID = new HashMap<String, Fragment>();
		fdtmcByName = new HashMap<String, FDTMC>();
	}

	public HashMap<String, FDTMC> getFdtmcByName() {
		return fdtmcByName;
	}

	public void initialize() throws InvalidTagException, UnsupportedFragmentTypeException {

		ADReader adParser = new ADReader(this.xmlFile, 0);
		adParser.retrieveActivities();
		this.adParsers.add(adParser);

		boolean hasNext = false;
		int index = 0;
		do {
			SDReader sdParser = new SDReader(this.xmlFile, index);
			sdParser.retrieveLifelines();
			sdParser.retrieveMessages();
			sdParser.traceDiagram();
			sdByID.put(sdParser.getSd().getId(), sdParser.getSd());
			this.sdParsers.add(sdParser);
			hasNext = sdParser.hasNext();
			index++;
		} while (hasNext);
		linkSdToActivity(this.adParsers.get(0));

		adParser.printAll();
		for (SDReader sdp : this.sdParsers) {
			sdp.printAll();
		}
	}

	public void linkSdToActivity(ADReader ad) {
		for (Activity a : ad.getActivities()) {
			if (a.getSdID() != null) {
				a.setSd(sdByID.get(a.getSdID()));
			}
		}
	}

	public void transform() {
		for (ADReader adParser : this.adParsers) {
			transformSingleAD(adParser);
		}

		for (SDReader sdParser : this.sdParsers) {
			transformSingleSD(sdParser.getSd());
		}
	}

	public void transformSingleAD(ADReader adParser) {
		FDTMC fdtmc = new FDTMC();
		State init;
		
		fdtmc.setVariableName("s" + adParser.getName());
		fdtmcByName.put(adParser.getName(), fdtmc);

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
				targetState = fdtmc.createState();
				fdtmc.createTransition(fdtmcState, targetState, sourceAct.getName(), "r"
						+ sourceAct.getName());
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
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
				targetState = fdtmc.createState();
				fdtmc.createTransition(fdtmcState, targetState, "", adEdge.getGuard());
				
				/* continue path */
				for (Edge e : targetAct.getOutgoing()) {
					transformPath(fdtmc, targetState, e);
				}
				if (targetAct.getType().equals(ActivityType.finalNode)) {
					stateByActID.put(targetAct.getId(), targetState);
					fdtmc.createTransition(targetState, targetState, "", "1.0");
				}
			} else { // atividade target ja foi criada
				fdtmc.createTransition(fdtmcState, targetState, "", adEdge.getGuard());
				/* end path */
			}
		} else if (sourceAct.getType().equals(ActivityType.merge)) {

		} else if (sourceAct.getType().equals(ActivityType.fork)) {

		} else if (sourceAct.getType().equals(ActivityType.join)) {

		}
		
		// colocar if final em todos os casos possiveis
		
		// chamar transorm path pra cada outgoing

	}

	public void transformSingleSD(Fragment fragment) {
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
					fdtmc.createTransition(source, success, ((Message) n).getName(), "r"
							+ ((Message) n).getReceiver().getName());
					fdtmc.createTransition(source, error, ((Message) n).getName(), "1-r"
							+ ((Message) n).getReceiver().getName());
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
					fdtmc.createTransition(source, target, ((Message) n).getName(), "r"
							+ ((Message) n).getReceiver().getName());
					fdtmc.createTransition(source, error, ((Message) n).getName(), "1-r"
							+ ((Message) n).getReceiver().getName());
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
	}
}
