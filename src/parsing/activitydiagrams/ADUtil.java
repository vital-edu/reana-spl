package parsing.activitydiagrams;

public class ADUtil {

	public static void printAll(ADReader adParser) { // jogar pra cima?? DiagramAPI
		System.out.print("Activity Diagram " + (adParser.getIndex() + 1) + ": " + adParser.getName() + "\n\n");
		printInSequence(adParser);
		System.out.print("\n\n");
	}

	public static void printInSequence(ADReader adParser) {

		System.out.println("Activities:");
		for (Activity a : adParser.getActivities()) {
			a.print();
			if (!a.getIncoming().isEmpty()) {
				System.out.println("\tIncoming Edges:");
				for (Edge e : a.getIncoming()) {
					System.out.print("\t\t");
					e.print();
				}
			}
			if (!a.getOutgoing().isEmpty()) {
				System.out.println("\tOutgoing Edges:");
				for (Edge e : a.getOutgoing()) {
					System.out.print("\t\t");
					e.print();
				}
			}
		}
		System.out.println();
	}
}
