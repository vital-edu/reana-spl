package Modeling.ActivityDiagrams;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ADReader {
	private int index;
	private String name;
	private boolean next;
	private HashMap<String, Activity> activitiesByID;
	private HashMap<String, Edge> edgesByID;
	private Document doc;
	private ArrayList<Activity> activities;
	private ArrayList<Edge> edges;
	//private ArrayList<Activity> mergeActivities;
	//private ArrayList<Activity> forkActivities;

	public void printAll() { // jogar pra cima?? DiagramAPI
		System.out.print("Activity Diagram " + (this.index + 1) + ": " + this.name + "\n\n");
		printInSequence();
		System.out.print("\n\n");
	}

	public void printInSequence() {
		
		System.out.println("Activities:");
		for (Activity a : this.activities) {
			a.print();
			if (a.getIncoming().size() != 0) {
				System.out.println("\tIncoming Edges:");
				for (Edge e : a.getIncoming()) {
					System.out.print("\t\t");
					e.print();
				}
			}
			if (a.getOutgoing().size() != 0) {
				System.out.println("\tOutgoing Edges:");
				for (Edge e : a.getOutgoing()) {
					System.out.print("\t\t");
					e.print();
				}
			}
		}
		System.out.println();
	}

	public ADReader(File xmlFile, int index) {
		this.index = index;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// this.coverage = new HashMap<Lifeline, ArrayList<String>>();
			this.doc = db.parse(xmlFile);
			this.doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public ArrayList<Activity> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<Activity> activities) {
		this.activities = activities;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}

	public boolean hasNext() {
		return next;
	}

	public void retrieveActivities() {
		org.w3c.dom.Node ad;
		ArrayList<org.w3c.dom.Node> adList = new ArrayList<org.w3c.dom.Node>();
		NodeList nodes = this.doc.getElementsByTagName("packagedElement");
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getAttributes().getNamedItem("xmi:type") != null) {
				String xmiType = nodes.item(i).getAttributes().getNamedItem("xmi:type")
						.getTextContent();
				if (xmiType != null && xmiType.equals("uml:Activity")) {
					ad = nodes.item(i);
					adList.add(ad);
				}
			}
		}

		if ((this.index + 1) == adList.size())
			this.next = false;
		else
			this.next = true;

		org.w3c.dom.Node node = adList.get(this.index);
		this.name = node.getAttributes().getNamedItem("name").getTextContent();
		NodeList elements = node.getChildNodes();
		this.activities = new ArrayList<Activity>();

		for (int s = 0; s < elements.getLength(); s++) {
			if (elements.item(s).getNodeName().equals("node")) {
				Activity tmp;
				if (elements.item(s).getAttributes().getNamedItem("name") != null) {
					tmp = new Activity(elements.item(s).getAttributes().getNamedItem("xmi:id")
							.getTextContent(), elements.item(s).getAttributes()
							.getNamedItem("name").getTextContent(), elements.item(s)
							.getAttributes().getNamedItem("xmi:type").getTextContent());
				} else {
					tmp = new Activity(elements.item(s).getAttributes().getNamedItem("xmi:id")
							.getTextContent(), null, elements.item(s).getAttributes()
							.getNamedItem("xmi:type").getTextContent());
				}
				if (elements.item(s).getAttributes().getNamedItem("behavior") != null) {
					tmp.setSdID(elements.item(s).getAttributes().getNamedItem("behavior").getTextContent());
				} else {
					tmp.setSdID(null);
				}
				
				this.activities.add(tmp);
			}
		}
		resetActivitiesByID();
		retrieveEdges(node);
		solveActivities(node);
	}

	public void resetActivitiesByID() {
		this.activitiesByID = new HashMap<String, Activity>();
		for (Activity a : this.activities) {
			this.activitiesByID.put(a.getId(), a);
		}
	}

	public void retrieveEdges(org.w3c.dom.Node node) {
		NodeList elements = node.getChildNodes();
		this.edges = new ArrayList<Edge>();

		for (int s = 0; s < elements.getLength(); s++) {
			if (elements.item(s).getNodeName().equals("edge")) {
				Edge tmp;
				if (elements.item(s).getAttributes().getNamedItem("name") != null) {
					tmp = new Edge(elements.item(s).getAttributes().getNamedItem("xmi:id")
							.getTextContent(), elements.item(s).getAttributes()
							.getNamedItem("name").getTextContent(), elements.item(s)
							.getAttributes().getNamedItem("xmi:type").getTextContent());
				} else {
					tmp = new Edge(elements.item(s).getAttributes().getNamedItem("xmi:id")
							.getTextContent(), null, elements.item(s).getAttributes()
							.getNamedItem("xmi:type").getTextContent());
				}
				tmp.setSource(this.activitiesByID.get(elements.item(s).getAttributes()
						.getNamedItem("source").getTextContent()));
				tmp.setTarget(this.activitiesByID.get(elements.item(s).getAttributes()
						.getNamedItem("target").getTextContent()));

				NodeList infoNodes = elements.item(s).getChildNodes();
				for (int i = 0; i < infoNodes.getLength(); i++) {
					if (infoNodes.item(i).getNodeName().equals("guard")) {
						NodeList guardNodes = infoNodes.item(i).getChildNodes();
						for (int j = 0; j < guardNodes.getLength(); j++) {
							if (guardNodes.item(j).getNodeName().equals("body")) {
								tmp.setGuard(guardNodes.item(j).getTextContent());
								break;
							}
						}
						break;
					}
				}
				this.edges.add(tmp);
			}
		}
		resetEdgesByID();
	}

	public void resetEdgesByID() {
		this.edgesByID = new HashMap<String, Edge>();
		for (Edge e : this.edges) {
			this.edgesByID.put(e.getId(), e);
		}
	}

	public void solveActivities(org.w3c.dom.Node node) {
		NodeList elements = node.getChildNodes();
		for (int s = 0; s < elements.getLength(); s++) {
			if (elements.item(s).getNodeName().equals("node")) {
				Activity activity = this.activitiesByID.get(elements.item(s).getAttributes()
						.getNamedItem("xmi:id").getTextContent());
				NodeList edges = elements.item(s).getChildNodes();
				for (int t = 0; t < edges.getLength(); t++) {
					if (edges.item(t).getNodeName().equals("incoming")) {
						activity.addIncoming(this.edgesByID.get(edges.item(t).getAttributes()
								.getNamedItem("xmi:idref").getTextContent()));
					} else if (edges.item(t).getNodeName().equals("outgoing")) {
						activity.addOutgoing(this.edgesByID.get(edges.item(t).getAttributes()
								.getNamedItem("xmi:idref").getTextContent()));
					}
				}
			}
		}
		orderActivities();
	}

	public void orderActivities() {
		int i = 0, j;
		LinkedList<Activity> queue = new LinkedList<Activity>();
		Activity target, temp;

		j = -1;
		for (Activity a : this.activities) {
			j++;
			if (a.getType().equals(ActivityType.initialNode)) {
				if (!this.activities.get(i).equals(a)) { // PEGAR EXEMPLO P
															// TESTAR AQUI
					temp = this.activities.get(i);
					this.activities.set(i, this.activities.get(j));
					this.activities.set(j, temp);
				}
				a.setOrdered(true);
				i++;
				queue.add(a);
				break;
			}
		}

		while (!queue.isEmpty()) {
			for (Edge e : queue.element().getOutgoing()) {
				target = e.getTarget();
				if (!target.isOrdered()) { // nao esta ordenado
					if (!this.activities.get(i).equals(target)) { // ordem errada
						j = -1;
						for (Activity a : this.activities) {
							j++;
							if (a.equals(target)) { // j:posicao do target
													// i:posicao atual ordenacao
								temp = this.activities.get(i);
								this.activities.set(i, this.activities.get(j));
								this.activities.set(j, temp);
								break;
							}
						}
					}
					target.setOrdered(true); // marca ordem target certa
					i++;
					queue.add(target); // poe target na fila
				}
			}
			queue.poll(); // tira o primeiro da fila
		}
	}
}
