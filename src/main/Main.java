package main;

import java.io.File;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;


import parser.InvalidTagException;
import parser.DiagramAPI;
import parser.UnsupportedFragmentTypeException;

public class Main {
	public static void main(String[] args) throws InvalidTagException, UnsupportedFragmentTypeException {
		File xmlFile = new File("model.xml");

		DiagramAPI diagram = new DiagramAPI(xmlFile);
		diagram.initialize();
		diagram.transform();

	}
}
