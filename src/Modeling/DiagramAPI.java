package Modeling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.State;
import Modeling.ActivityDiagrams.ADReader;
import Modeling.ActivityDiagrams.Activity;
import Modeling.Exceptions.InvalidTagException;
import Modeling.Exceptions.UnsupportedFragmentTypeException;
import Modeling.SequenceDiagrams.Fragment;
import Modeling.SequenceDiagrams.SDReader;

public class DiagramAPI {
	// Attributes
		private final File xmlFile;
		private ArrayList<SDReader> sdParsers;
		private ArrayList<ADReader> adParsers;
		private HashMap<String, Fragment> sdByID;
		private HashMap<String, FDTMC> fdtmcByName;
		private HashMap<String, State> stateByActID;
	
	// Constructors
		public DiagramAPI(File xmlFile) {
			this.xmlFile = xmlFile;
			adParsers = new ArrayList<ADReader>();
			sdParsers = new ArrayList<SDReader>();
			sdByID = new HashMap<String, Fragment>();
			fdtmcByName = new HashMap<String, FDTMC>();
			stateByActID = new HashMap<String, State>();
		}
		
	// Relevant public methods
		
		/**
		 * Initializes the model transformation activities,
		 * starting from parsing the XMI file and 
		 * then applying the transformation functions
		 * @throws InvalidTagException 
		 * @throws UnsupportedFragmentTypeException 
		 */
		public void initialize() throws UnsupportedFragmentTypeException, InvalidTagException {
			ADReader adParser = new ADReader(this.xmlFile, 0);
			adParser.retrieveActivities();
			this.adParsers.add(adParser);
			
			boolean hasNext = false;
			int index = 0;
			do {
				SDReader sdParser = new SDReader(this.xmlFile, index);
				sdParser.traceDiagram();
				sdByID.put(sdParser.getSD().getId(), sdParser.getSD());
				this.sdParsers.add(sdParser);
				hasNext = sdParser.hasNext();
				index++;
			} while (hasNext);
			linkSdToActivity(this.adParsers.get(0));
		}
	
	// Relevant private methods
		private void linkSdToActivity(ADReader ad) {
			for (Activity a : ad.getActivities()) {
				if (a.getSdID() != null) {
					a.setSd(sdByID.get(a.getSdID()));
				}
			}
		}
		
	// Getters and Setters

		public ArrayList<SDReader> getSdParsers() {
			return sdParsers;
		}

		public void setSdParsers(ArrayList<SDReader> sdParsers) {
			this.sdParsers = sdParsers;
		}

		public ArrayList<ADReader> getAdParsers() {
			return adParsers;
		}

		public void setAdParsers(ArrayList<ADReader> adParsers) {
			this.adParsers = adParsers;
		}

		public HashMap<String, Fragment> getSdByID() {
			return sdByID;
		}

		public void setSdByID(HashMap<String, Fragment> sdByID) {
			this.sdByID = sdByID;
		}

		public HashMap<String, FDTMC> getFdtmcByName() {
			return fdtmcByName;
		}

		public void setFdtmcByName(HashMap<String, FDTMC> fdtmcByName) {
			this.fdtmcByName = fdtmcByName;
		}

		public HashMap<String, State> getStateByActID() {
			return stateByActID;
		}

		public void setStateByActID(HashMap<String, State> stateByActID) {
			this.stateByActID = stateByActID;
		}

		public File getXmlFile() {
			return xmlFile;
		}
}
