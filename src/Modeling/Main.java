package Modeling;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.DOMException;

import fdtmc.FDTMC;
import Parsing.ActivityDiagrams.ADUtil;
import Parsing.Exceptions.InvalidNodeClassException;
import Parsing.Exceptions.InvalidNodeType;
import Parsing.Exceptions.InvalidNumberOfOperandsException;
import Parsing.Exceptions.InvalidTagException;
import Parsing.Exceptions.UnsupportedFragmentTypeException;
import Parsing.SequenceDiagrams.SDReader;
import Parsing.SequenceDiagrams.SDUtil;

public class Main {
	private static HashMap<String, FDTMC> fdtmcByName;

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
