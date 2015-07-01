package main;

import java.io.File;
import java.util.HashMap;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import Modeling.DiagramAPIOld;
import Modeling.InvalidTagException;
import Modeling.UnsupportedFragmentTypeException;
import Modeling.SequenceDiagrams.Message;

public class Main {
	private static HashMap<String, FDTMC> fdtmcByName;
	
	public static void main(String[] args) throws InvalidTagException, UnsupportedFragmentTypeException {
		File xmlFile = new File("model.xml");
		
		DiagramAPIOld diagram = new DiagramAPIOld(xmlFile);
		diagram.initialize();
		diagram.transform();
		
		fdtmcByName = diagram.getFdtmcByName();
	}
}
