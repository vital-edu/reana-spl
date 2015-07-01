package Modeling.SequenceDiagrams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Modeling.InvalidTagException;
import Modeling.UnsupportedFragmentTypeException;

/**
 * Classe responsável por realizar o parser de um fSD 
 * e montar os objetos para serem consumidos pela classe DiagramAPI
 * @author abiliooliveira
 *
 */
public class SDReader {
	// Atributos
		private int index;
		private boolean next;
		private HashMap<Lifeline, ArrayList<String>> coverage;
		private HashMap<String, Lifeline> lifelinesByID;
		private HashMap<String, Message> messagesByID;
		private Document doc;
		private ArrayList<Lifeline> lifelines;
		private ArrayList<Message> messages;
		private Fragment sd;
	
	// Construtores
		public SDReader(File xmiFile, int index) {
			this.index = index;
			try { 
				this.coverage = new HashMap<Lifeline, ArrayList<String>>();
				this.lifelinesByID = new HashMap<String, Lifeline>();
				this.messagesByID = new HashMap<String, Message>();
				this.lifelines = new ArrayList<Lifeline>();
				this.messages = new ArrayList<Message>();
				setDoc(xmiFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	// Métodos
		/**
		 * Realiza o parse do xmi buscando as Lifelines do SD em questão
		 * @throws InvalidTagException
		 * @throws UnsupportedFragmentTypeException
		 */
		public void retrieveLifelines() throws InvalidTagException, UnsupportedFragmentTypeException {
			NodeList nodes = this.doc.getElementsByTagName("ownedBehavior");
			if (this.index == nodes.getLength() - 1)
				this.next = false;
			else
				this.next = true;
			
			org.w3c.dom.Node node = nodes.item(this.index);
			NodeList elements = node.getChildNodes();
			
			for (int s = 0; s < elements.getLength(); s++) {
				
				if (elements.item(s).getNodeName().equals("lifeline")) {
					NamedNodeMap sAttrs = elements.item(s).getAttributes();
					NodeList sChilds = elements.item(s).getChildNodes();
					Lifeline tmp = new Lifeline(sAttrs.getNamedItem("xmi:id").getTextContent());
					ArrayList<String> coveredBy = new ArrayList<String>();
					
					tmp.setLink(sAttrs.getNamedItem("represents").getTextContent());
					if (sAttrs.getNamedItem("name") != null) {
						tmp.setName(sAttrs.getNamedItem("name").getTextContent());
					}
					
					for (int j = 0; j < sChilds.getLength(); j++) {
						if (sChilds.item(j).getNodeName().equals("coveredBy"))
							coveredBy.add(sChilds.item(j).getAttributes().getNamedItem("xmi:idref").getTextContent());
					} 
					
					this.coverage.put(tmp, coveredBy);
					this.lifelines.add(tmp);
					this.lifelinesByID.put(tmp.getId(), tmp);
				}
			}
			
			for (int s = 0; s < elements.getLength(); s++) {
				if (elements.item(s).getNodeName().equals("ownedAttribute")) {
					for (int j = 0; j < this.lifelines.size(); j++) {
						if (((Lifeline) this.lifelines.get(j)).getLink().equals(
								elements.item(s).getAttributes().getNamedItem("xmi:id")
										.getTextContent())) {
							((Lifeline) this.lifelines.get(j)).setName(elements.item(s).getAttributes()
									.getNamedItem("name").getTextContent().replace('\n', ' '));
						}
					}
				}
			}
		}
		
	// Getters and Setters
		public int getIndex() {
			return index;
		}
		
		public void setIndex(int index) {
			this.index = index;
		}
		
		public boolean isNext() {
			return next;
		}
		
		public void setNext(boolean next) {
			this.next = next;
		}
		
		public HashMap<Lifeline, ArrayList<String>> getCoverage() {
			return coverage;
		}
		
		public void setCoverage(HashMap<Lifeline, ArrayList<String>> coverage) {
			this.coverage = coverage;
		}
		
		public HashMap<String, Lifeline> getLifelinesByID() {
			return lifelinesByID;
		}
		
		public void setLifelinesByID(HashMap<String, Lifeline> lifelinesByID) {
			this.lifelinesByID = lifelinesByID;
		}
		
		public HashMap<String, Message> getMessagesByID() {
			return messagesByID;
		}
		
		public void setMessagesByID(HashMap<String, Message> messagesByID) {
			this.messagesByID = messagesByID;
		}
		
		public Document getDoc() {
			return doc;
		}
		
		public void setDoc(Document doc) {
			this.doc = doc;
		}
		
		public void setDoc(File xmiFile) throws ParserConfigurationException, SAXException, IOException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.doc = db.parse(xmiFile);
			this.doc.getDocumentElement().normalize();
		}
		
		public ArrayList<Lifeline> getLifelines() {
			return lifelines;
		}
		
		public void setLifelines(ArrayList<Lifeline> lifelines) {
			this.lifelines = lifelines;
		}
		
		public ArrayList<Message> getMessages() {
			return messages;
		}
		
		public void setMessages(ArrayList<Message> messages) {
			this.messages = messages;
		}
		
		public Fragment getSD() {
			return sd;
		}
		
		public void setSD(Fragment sd) {
			this.sd = sd;
		}
}
