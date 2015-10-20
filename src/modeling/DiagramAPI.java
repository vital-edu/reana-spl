package modeling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;

import parsing.activitydiagrams.ADReader;
import parsing.activitydiagrams.Activity;
import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.exceptions.InvalidTagException;
import parsing.exceptions.UnsupportedFragmentTypeException;
import parsing.sequencediagrams.Fragment;
import parsing.sequencediagrams.SDReader;
import tool.RDGNode;
import transformation.Transformer;
import fdtmc.FDTMC;

public class DiagramAPI {
	// Attributes

		private final File xmlFile;
		private List<SDReader> sdParsers;
		private ADReader adParser;
		private Map<String, Fragment> sdByID;
		private Transformer transformer;

	// Constructors

		public DiagramAPI(File xmlFile) throws UnsupportedFragmentTypeException, InvalidTagException {
			this.xmlFile = xmlFile;
			adParser = null;
			sdParsers = new ArrayList<SDReader>();
			sdByID = new HashMap<String, Fragment>();
			transformer = new Transformer();

			initialize();
		}

	// Relevant public methods


		/**
		 * Triggers the applicable transformations, either AD or SD based
		 * @throws InvalidNumberOfOperandsException
		 * @throws InvalidNodeClassException
		 */
		public RDGNode transform() throws InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
			RDGNode topLevel = transformer.transformSingleAD(adParser);
			for (SDReader sdParser : this.sdParsers) {
				RDGNode sdRDG = transformer.transformSingleSD(sdParser.getSD());
				topLevel.addDependency(sdRDG);
			}
			return topLevel;
		}

		public void measureSizeModel (FDTMC fdtmc) {
			transformer.measureSizeModel(fdtmc);
		}

		public void printNumberOfCalls (String name) {
			transformer.printNumberOfCalls(name);
		}

	// Relevant private methods

		/**
		 * Initializes the model transformation activities,
		 * starting from parsing the XMI file and
		 * then applying the transformation functions
		 * @throws InvalidTagException
		 * @throws UnsupportedFragmentTypeException
		 * @throws InvalidGuardException
		 * @throws DOMException
		 */
		private void initialize() throws UnsupportedFragmentTypeException, InvalidTagException {
		    ADReader tmpAdParser = new ADReader(this.xmlFile, 0);
		    tmpAdParser.retrieveActivities();
		    this.adParser = tmpAdParser;

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
		    linkSdToActivity(this.adParser);
		}

		/**
		 * Links activities of an AD to their respective SD
		 * @param ad
		 */
		private void linkSdToActivity(ADReader ad) {
			for (Activity a : ad.getActivities()) {
				if (a.getSdID() != null) {
					a.setSd(sdByID.get(a.getSdID()));
				}
			}
		}

	// Getters and Setters

		public Map<String, FDTMC> getFdtmcByName() {
			return transformer.getFdtmcByName();
		}

		public List<SDReader> getSdParsers() {
			return sdParsers;
		}

		public ADReader getAdParser() {
			return adParser;
		}

}
