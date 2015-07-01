package Modeling.SequenceDiagrams;

import Modeling.*;

/**
 * Conjunto de métodos estáticas para lidar com I/O de SDs
 * @author abiliooliveira
 *
 */
public class SDUtil {
	
	/**
	 * Para DEBUG. 
	 * Imprime no STDOUT as informações de uma Mensagem num formato pertinente.
	 * @param m
	 */
	public static void printMessage(Message m) {
		System.out.println("[" + m.getName() + "]: "
				+ m.getSender().getName() + "->"
				+ m.getReceiver().getName() + " (" + m.getType()
				+ ") p = " + m.getProb() + "; egy: " + m.getEnergy() + "; ex: "
				+ m.getExecTime());
	}
	
	/**
	 * Para DEBUG.
	 * Imprime no STDOUT as informações de um Fragmento num formato pertinente.
	 * @param sd
	 * @param index
	 */
	public static void printInSequence(Fragment fragment, String indent) {
		System.out.print(indent);
		System.out.println("Lifelines: ");
		for (Lifeline l: fragment.getLifelines()) {
			System.out.print(indent);
			System.out.println(l.getName());
		}
		System.out.println();
		System.out.print(indent);
		System.out.println("Nodes: ");
		for (Node n: fragment.getNodes()) {
			System.out.print(indent);
			if (n.getClass().equals(Fragment.class)) {
				Fragment f = (Fragment)n;
				System.out.println(f.getName());
				printInSequence(f, indent+"\t");
			} else if (n.getClass().equals(Operand.class)) {
				Operand o = (Operand)n;
				System.out.println("Guard:" + o.getGuard());
				for (Node n1: o.getNodes()) {
					if (n1.getClass().equals(Message.class)) {
						printMessage((Message)n1);
					}
				}
			}else if (n.getClass().equals(Message.class)) {
				printMessage((Message)n);
			}
		} 
	}
	
	/**
	 * Para DEBUG.
	 * Imprime num formato pertinente as informações de interesse de uma classe SDReader
	 * @param sdr
	 */
	public static void printAll(SDReader sdr) {
		System.out.print("Sequence Diagram "+ (sdr.getIndex() + 1) + ": " + sdr.getSD().getName() + "\n\n");
		printInSequence(sdr.getSD(), "");
	} 
	
}
