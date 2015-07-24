package Modeling;

import java.io.File;
import java.util.HashMap;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import Parsing.InvalidTagException;
import Parsing.UnsupportedFragmentTypeException;

public class Main {
	private static HashMap<String, FDTMC> fdtmcByName;
	
	public static void main(String[] args) throws InvalidTagException, UnsupportedFragmentTypeException {
		File xmlFile = new File("modeling.xml");
		
		DiagramAPI diagram = new DiagramAPI(xmlFile);
		diagram.initialize();
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
