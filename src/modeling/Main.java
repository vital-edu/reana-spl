package modeling;

import java.io.File;
import java.util.Map;

import org.w3c.dom.DOMException;

import parsing.activitydiagrams.ADUtil;
import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.exceptions.InvalidTagException;
import parsing.exceptions.UnsupportedFragmentTypeException;
import parsing.sequencediagrams.SDReader;
import parsing.sequencediagrams.SDUtil;
import fdtmc.FDTMC;

public class Main {
	private static Map<String, FDTMC> fdtmcByName;

	public static void main(String[] args) throws InvalidTagException, UnsupportedFragmentTypeException, DOMException, InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
		File xmlFile = new File("modeling.xml");

		DiagramAPI diagram = new DiagramAPI(xmlFile);
		ADUtil.printAll(diagram.getAdParser());
		for (SDReader sdp : diagram.getSdParsers()) {
			SDUtil.printAll(sdp);
		}
		diagram.transform();

		fdtmcByName = diagram.getFdtmcByName();

		/* Results: */
		String msg = new String();
		msg += "****************************************************************" + "\n";
		msg += "Results " + "\n";
		msg += "****************************************************************" + "\n\n";
		System.out.print(msg);

		for (String name : fdtmcByName.keySet()) {
			FDTMC f = fdtmcByName.get(name);

			System.out.print("Model: " + name  + "\n\t" +
								"number of calls: ");
			diagram.printNumberOfCalls(name);
			System.out.print("\t");
			diagram.measureSizeModel(f);
		}
	}
}
