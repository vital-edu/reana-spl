package main;

import java.io.File;
import java.util.HashMap;

import org.w3c.dom.DOMException;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import Modeling.DiagramAPI;
import Modeling.ActivityDiagrams.ADUtil;
import Modeling.Exceptions.InvalidNodeClassException;
import Modeling.Exceptions.InvalidNumberOfOperandsException;
import Modeling.Exceptions.InvalidTagException;
import Modeling.Exceptions.UnsupportedFragmentTypeException;
import Modeling.SequenceDiagrams.SDReader;
import Modeling.SequenceDiagrams.SDUtil;

public class Main {
	private static HashMap<String, FDTMC> fdtmcByName;
	
	public static void main(String[] args) throws InvalidTagException, UnsupportedFragmentTypeException, DOMException, InvalidNumberOfOperandsException, InvalidNodeClassException {
		File xmlFile = new File("model2.xml");
		
		DiagramAPI diagram = new DiagramAPI(xmlFile);
		diagram.initialize();
		ADUtil.printAll(diagram.getAdParsers().get(0));
		for (SDReader sdp : diagram.getSdParsers()) {
			SDUtil.printAll(sdp);
		}
		diagram.transform();
		
		fdtmcByName = diagram.getFdtmcByName();
	}
}
