package parsing.SplGeneratorModels;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import parsing.SplGeneratorModels.Fragment;
import parsing.SplGeneratorModels.Lifeline;
import parsing.SplGeneratorModels.Message;
import parsing.SplGeneratorModels.SequenceDiagram;
import parsing.SplGeneratorModels.SequenceDiagramElement;
import parsing.SplGeneratorModels.SPLFilePersistence;
import tool.RDGNode;
import fdtmc.*;

public class SequenceDiagramTransformer {

	RDGNode root;
	HashMap<String, fdtmc.State> fdtmcStateById;

	public SequenceDiagramTransformer() {
		fdtmcStateById = new HashMap<String, fdtmc.State>();
		root = null;
	}

	public RDGNode transformSD(SequenceDiagram s) {
		FDTMC f = new FDTMC();
		f.setVariableName(s.getName() + "_s");
		RDGNode answer = new RDGNode(s.getName(), s.getGuardCondition(), f);
		root = answer;

//		LinkedList<SequenceDiagramElement> sde = (LinkedList<SequenceDiagramElement>) s
//				.getElements().clone();
		
		State s0 = f.createInitialState();
		f.createErrorState();
//		State target = transformSdElement(sde, f);
		State target = transformSdElement(s.getElements(), f);
		f.createTransition(s0, target, "", Double.toString(1.0));
		// State s0 = transformSdElement(sde, f);
		// s0.setLabel(FDTMC.INITIAL_LABEL);

		// System.out.println(f);
		// SPLFilePersistence.fdtmc2Dot(f, s.getName());

		return answer;
	}

	private State transformSdElement(LinkedList<SequenceDiagramElement> sde,
			FDTMC f) {
		State source;
		State target;

		// SequenceDiagramElement e = sde.removeFirst();
		SequenceDiagramElement e = null;
		String sdClass;
		if (sde.isEmpty()) {
			target = f.createSuccessState();
			return target;
		} else {
			e = sde.removeFirst();
			sdClass = e.getClass().getSimpleName();
			target = transformSdElement(sde, f);
		}

		source = f.createState();

		switch (sdClass) {
		case "Message":
			Message m = (Message) e;
			if (m.getType() == Message.SYNCHRONOUS) {
				double reliability = new BigDecimal(m.getProbability()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				double complement = new BigDecimal(1-m.getProbability()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				f.createTransition(source, target, m.getName(),
						Double.toString(reliability));
				f.createTransition(source, f.getErrorState(), m.getName(),
						Double.toString(complement));
//				f.createTransition(source, target, m.getName(),
//						Double.toString(m.getProbability()));
//				f.createTransition(source, f.getErrorState(), m.getName(),
//						Double.toString(1 - m.getProbability()));
				System.out.println("sync");
			}
			if (m.getType() == Message.ASYNCHRONOUS) {
				double reliability = new BigDecimal(m.getProbability()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				double complement = new BigDecimal(1-m.getProbability()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				f.createTransition(source, target, m.getName(),
						Double.toString(reliability));
				f.createTransition(source, f.getErrorState(), m.getName(),
						Double.toString(complement));
//				f.createTransition(source, target, m.getName(),
//						Double.toString(m.getProbability()));
//				f.createTransition(source, f.getErrorState(), m.getName(),
//						Double.toString(1 - m.getProbability()));
				System.out.println("async");
			}
			break;

		case "Fragment":
			Fragment fr = (Fragment) e;
			if (fr.getType() == Fragment.OPTIONAL) {
				f.createTransition(source, target, "", fr.getSequenceDiagrams()
						.getFirst().getName());
				f.createTransition(source, f.getErrorState(), "", "1-"
						+ fr.getSequenceDiagrams().getFirst().getName());

				for (SequenceDiagram s : fr.getSequenceDiagrams()) {
					SequenceDiagramTransformer transformer = new SequenceDiagramTransformer();
					this.root.addDependency(transformer.transformSD(s));
				}
			} else if (fr.getType() == Fragment.ALTERNATIVE) {
				for (SequenceDiagram s : fr.getSequenceDiagrams()) {
					target = transformSdElement(s.getElements(), f);
					double probability = new BigDecimal(1 / fr.getSequenceDiagrams().size()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
					f.createTransition(source, target, "alt", Double
							.toString(probability));
//					f.createTransition(source, target, "alt", Double
//							.toString(1 / fr.getSequenceDiagrams().size()));
				}
			}
			break;

		default:
			break;
		}

		return source;
	}

}
