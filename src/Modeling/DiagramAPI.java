package Modeling;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.DOMException;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.State;
import Modeling.ActivityDiagrams.ADReader;
import Modeling.ActivityDiagrams.Activity;
import Modeling.ActivityDiagrams.ActivityType;
import Modeling.ActivityDiagrams.Edge;
import Modeling.Exceptions.InvalidGuardException;
import Modeling.Exceptions.InvalidNodeClassException;
import Modeling.Exceptions.InvalidNumberOfOperandsException;
import Modeling.Exceptions.InvalidTagException;
import Modeling.Exceptions.UnsupportedFragmentTypeException;
import Modeling.SequenceDiagrams.Fragment;
import Modeling.SequenceDiagrams.Message;
import Modeling.SequenceDiagrams.Operand;
import Modeling.SequenceDiagrams.SDReader;

public class DiagramAPI {
	// Attributes
	
		private final File xmlFile;
		private ArrayList<SDReader> sdParsers;
		private ArrayList<ADReader> adParsers;
		private HashMap<String, Fragment> sdByID;
		private HashMap<String, FDTMC> fdtmcByName;
		private HashMap<String, State> stateByActID;
	
	// Constructors
		
		public DiagramAPI(File xmlFile) {
			this.xmlFile = xmlFile;
			adParsers = new ArrayList<ADReader>();
			sdParsers = new ArrayList<SDReader>();
			sdByID = new HashMap<String, Fragment>();
			fdtmcByName = new HashMap<String, FDTMC>();
			stateByActID = new HashMap<String, State>();
		}
		
	// Relevant public methods
		
		/**
		 * Initializes the model transformation activities,
		 * starting from parsing the XMI file and 
		 * then applying the transformation functions
		 * @throws InvalidTagException 
		 * @throws UnsupportedFragmentTypeException 
		 * @throws InvalidGuardException 
		 * @throws DOMException 
		 */
		public void initialize() throws UnsupportedFragmentTypeException, InvalidTagException, DOMException, InvalidGuardException {
			ADReader adParser = new ADReader(this.xmlFile, 0);
			adParser.retrieveActivities();
			this.adParsers.add(adParser);
			
			boolean hasNext = false;
			int index = 0;
			do {
				SDReader sdParser = new SDReader(this.xmlFile, index);
				sdParser.traceDiagram();
				sdByID.put(sdParser.getSD().getId(), sdParser.getSD());
				this.sdParsers.add(sdParser);
				hasNext = sdParser.hasNext();
				index++;
			} while (hasNext);
			linkSdToActivity(this.adParsers.get(0));
		}
		
		/**
		 * Triggers the applicable transformations, either AD or SD based
		 * @throws InvalidNumberOfOperandsException 
		 * @throws InvalidNodeClassException 
		 */
		public void transform() throws InvalidNumberOfOperandsException, InvalidNodeClassException {
			for (ADReader adParser : this.adParsers) {
				transformSingleAD(adParser);
			}
			
			for (SDReader sdParser : this.sdParsers) {
				transformSingleSD(sdParser);
			}
		}
		
		/**
		 * Transforms an AD to a fDTMC
		 * @param adParser
		 */
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
		
		/**
		 * Transform an SD to a fDTMC
		 * @param sdParser
		 * @throws InvalidNumberOfOperandsException 
		 * @throws InvalidNodeClassException 
		 */
		public void transformSingleSD(SDReader sdParser) throws InvalidNumberOfOperandsException, InvalidNodeClassException {
			FDTMC fdtmc = new FDTMC();
			State init, error, success, source;
			
			fdtmc.setVariableName("s" + sdParser.getSD().getName());
			fdtmcByName.put(sdParser.getSD().getName(), fdtmc);
			
			init = fdtmc.createState("init");
			error = fdtmc.createState("error");
			source = init;
			
			int i = 1;
			for (Node n : sdParser.getSD().getNodes()) {
				if (i++ >= sdParser.getSD().getNodes().size()) {
					success = fdtmc.createState("success");
					if (n.getClass().equals(Message.class)) {
						transformMessage(fdtmc, (Message)n, source, success, error);
					} else if (n.getClass().equals(Fragment.class)) {
						transformFragment(fdtmc, (Fragment)n, source, success, error);
					}
				} else {
					if (n.getClass().equals(Message.class)) {
						source = transformMessage(fdtmc, (Message)n, source, fdtmc.createState(), error);
					} else if (n.getClass().equals(Fragment.class)) {
						source = transformFragment(fdtmc, (Fragment)n, source, fdtmc.createState(), error);
					}
					
				}
			}
			System.out.println(fdtmc.toString());
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
			fdtmc.createTransition(source, target, msg.getName(), b.toString());
			fdtmc.createTransition(source, error, msg.getName(), a.subtract(b).toString());
			return target;
		}
		
		/**
		 * Distributes the fragment transformation method calls based on the the type of the Fragment
		 * @throws InvalidNumberOfOperandsException
		 * @throws InvalidNodeClassException
		 */
		private State transformFragment(FDTMC fdtmc, Fragment fragment, State source, State target, State error) throws InvalidNumberOfOperandsException, InvalidNodeClassException {
			switch(fragment.getType()) {
				case loop:
					return transformLoopFragment(fdtmc, fragment, source, target, error);
				case alternative:
					return transformAltFragment(fdtmc, fragment, source, target, error);
				case optional:
					return transformOptFragment(fdtmc, fragment, source, target, error);
				case parallel:
					return transformParallelFragment(fdtmc, fragment, source, target, error);
				default:
					break;
			}
			return null;
		}
		
		/**
		 * Recursively augments the fDTMC with $fragments information  
		 * @param fdtmc
		 * @param fragment: an fragment of type loop
		 * @param source: the fDTMC node that triggers or not the Fragment
		 * @param target: the fDTMC node that the fragment should return to
		 * @param error: the error state where message transmission failure should be transited to
		 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
		 * @throws InvalidNumberOfOperandsException
		 * @throws InvalidNodeClassException 
		 */
		private State transformLoopFragment(FDTMC fdtmc, Fragment fragment, State source, State target, State error) throws InvalidNumberOfOperandsException, InvalidNodeClassException {
			if (fragment.getNodes().size() > 1) throw new InvalidNumberOfOperandsException("An Loop fragment can only have 1 operand!");
			
			Operand operand = (Operand)fragment.getNodes().get(0);
			State featureStart = fdtmc.createState("init" + fragment.getName());
			State featureEnd = fdtmc.createState("end" + fragment.getName());
			
			fdtmc.createTransition(source, target, "not" + fragment.getName(), Float.toString(1 - operand.getGuard())); // not entering loop
			fdtmc.createTransition(source, featureStart, "in" + fragment.getName(), operand.getGuard().toString()); // into Feature
			
			// iterates through each of the Operand's nodes and augments the fdtmc
			for (Node n : operand.getNodes()) {
				if (n.getClass().equals(Message.class)) {
					transformMessage(fdtmc, (Message)n, featureStart, featureEnd, error);
				} else if (n.getClass().equals(Fragment.class)) {
					transformFragment(fdtmc, (Fragment)n, featureStart, featureEnd, error);
				}
			}
			
			fdtmc.createTransition(featureEnd, featureStart, "in" + fragment.getName(), operand.getGuard().toString()); // going back into the loop
			fdtmc.createTransition(featureEnd, target, "out" + fragment.getName(), Float.toString(1 - operand.getGuard())); // getting out of Feature
			
			return target;
		}
		
		/**
		 * Recursively augments the fdtmc with $fragments information  
		 * @param fdtmc
		 * @param fragment: a fragment of type alternative
		 * @param source: the fDTMC node that triggers or not the Fragment
		 * @param target: the fDTMC node that the fragment should return to
		 * @param error: the error state where message transmission failure should be transited to
		 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
		 * @throws InvalidNodeClassException
		 * @throws InvalidNumberOfOperandsException
		 */
		private State transformAltFragment(FDTMC fdtmc, Fragment fragment, State source, State target, State error) throws InvalidNodeClassException, InvalidNumberOfOperandsException {
			ArrayList<Node> operands = fragment.getNodes();
			int i = 1;
			
			Float featureProb = new Float(0); // stores the probability of entering the fragment
			for(Node node : operands) {
				if (!node.getClass().equals(Operand.class)) throw new InvalidNodeClassException("An Alt Fragment can only have Operand objects as Nodes!");
				
				Operand operand = (Operand)node; // to facilitate the nodes use
				State featureStart = fdtmc.createState("init" + fragment.getName() + i); // each alternative is a new Feature (an alternative is an Operand)
				State featureEnd = fdtmc.createState("end" + fragment.getName() + i);
				
				fdtmc.createTransition(source, featureStart, "in" + fragment.getName() + i, operand.getGuard().toString());
				
				// iterates through each of the Operand's nodes and augments the fdtmc
				for (Node n : operand.getNodes()) {
					if (n.getClass().equals(Message.class)) {
						transformMessage(fdtmc, (Message)n, featureStart, featureEnd, error);
					} else if (n.getClass().equals(Fragment.class)) {
						transformFragment(fdtmc, (Fragment)n, featureStart, featureEnd, error);
					}
				}
				
				fdtmc.createTransition(featureEnd, target, "out" + fragment.getName() + i, "1.0");
				
				featureProb += operand.getGuard();
				i++;
			}
			
			fdtmc.createTransition(source, target, "not" + fragment.getName(), Float.toString(1 - featureProb));
			return target;
		}
		
		/**
		 * Recursively augments the fdtmc with $fragments information  
		 * @param fdtmc
		 * @param fragment: a fragment of type optional
		 * @param source: the fDTMC node that triggers or not the Fragment
		 * @param target: the fDTMC node that the fragment should return to
		 * @param error: the error state where message transmission failure should be transited to
		 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
		 * @throws InvalidNumberOfOperandsException
		 * @throws InvalidNodeClassException 
		 */
		private State transformOptFragment(FDTMC fdtmc, Fragment fragment, State source, State target, State error) throws InvalidNumberOfOperandsException, InvalidNodeClassException {
			if (fragment.getNodes().size() > 1) throw new InvalidNumberOfOperandsException("An Opt fragment can only have 1 operand!");
			
			Operand operand = (Operand)fragment.getNodes().get(0);
			State featureStart = fdtmc.createState("init" + fragment.getName());
			State featureEnd = fdtmc.createState("end" + fragment.getName());
			
			fdtmc.createTransition(source, target, "not" + fragment.getName(), Float.toString(1 - operand.getGuard())); // not entering opt
			fdtmc.createTransition(source, featureStart, "in" + fragment.getName(), operand.getGuard().toString()); // into Feature
			
			// iterates through each of the Operand's nodes and augments the fdtmc
			for (Node n : operand.getNodes()) {
				if (n.getClass().equals(Message.class)) {
					transformMessage(fdtmc, (Message)n, featureStart, featureEnd, error);
				} else if (n.getClass().equals(Fragment.class)) {
					transformFragment(fdtmc, (Fragment)n, featureStart, featureEnd, error);
				}
			}
			
			fdtmc.createTransition(featureEnd, target, "out" + fragment.getName(), "1.0"); // getting out of Feature
			
			return target;
		}
		
		/**
		 * Recursively augments the fdtmc with $fragments information  
		 * @param fdtmc
		 * @param fragment: a fragment of type parallel
		 * @param source: the fDTMC node that triggers or not the Fragment
		 * @param target: the fDTMC node that the fragment should return to
		 * @param error: the error state where message transmission failure should be transited to
		 * @return the $target itself, the point in the fDTMC where the execution or not of this $fragment will transit to
		 */
		private State transformParallelFragment(FDTMC fdtmc, Fragment fragment, State source, State target, State error) {
			// TODO
			return null;
		}
		
		/**
		 * Links activities of an AD to their respective SD
		 * @param ad
		 */
		private void linkSdToActivity(ADReader ad) {
			for (Activity a : ad.getActivities()) {
				if (a.getSdID() != null) {
					a.setSd(sdByID.get(a.getSdID()));
				}
			}
		}
		
		/**
		 * transformSingleAD auxiliary method
		 * @param fdtmc
		 * @param fdtmcState
		 * @param adEdge
		 */
		private void transformPath(FDTMC fdtmc, State fdtmcState, Edge adEdge) {
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
		
	// Getters and Setters

		public ArrayList<SDReader> getSdParsers() {
			return sdParsers;
		}

		public void setSdParsers(ArrayList<SDReader> sdParsers) {
			this.sdParsers = sdParsers;
		}

		public ArrayList<ADReader> getAdParsers() {
			return adParsers;
		}

		public void setAdParsers(ArrayList<ADReader> adParsers) {
			this.adParsers = adParsers;
		}

		public HashMap<String, Fragment> getSdByID() {
			return sdByID;
		}

		public void setSdByID(HashMap<String, Fragment> sdByID) {
			this.sdByID = sdByID;
		}

		public HashMap<String, FDTMC> getFdtmcByName() {
			return fdtmcByName;
		}

		public void setFdtmcByName(HashMap<String, FDTMC> fdtmcByName) {
			this.fdtmcByName = fdtmcByName;
		}

		public HashMap<String, State> getStateByActID() {
			return stateByActID;
		}

		public void setStateByActID(HashMap<String, State> stateByActID) {
			this.stateByActID = stateByActID;
		}

		public File getXmlFile() {
			return xmlFile;
		}
}
