package main;

import java.io.File;
import java.util.HashMap;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import Modeling.DiagramAPI;
import Modeling.InvalidTagException;
import Modeling.Message;
import Modeling.UnsupportedFragmentTypeException;

public class Main {
	private static HashMap<String, FDTMC> fdtmcByName;
	
	public static void main(String[] args) throws InvalidTagException, UnsupportedFragmentTypeException {
		File xmlFile = new File("model.xml");
		
		DiagramAPI diagram = new DiagramAPI(xmlFile);
		diagram.initialize();
		diagram.transform();
		
		fdtmcByName = diagram.getFdtmcByName();
	}
}
