package Modeling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SDReader {
	private int index;
	private boolean next;
	private HashMap<Lifeline, ArrayList<String>> coverage;
	private HashMap<String, Lifeline> lifelinesByID;
	private HashMap<String, Message> messagesByID;
	private Document doc;
	private ArrayList<Lifeline> lifelines;
	private ArrayList<Message> messages;
	private Fragment sd;

	public void printAll() { // jogar pra cima?? DiagramAPI
		System.out
				.print("Sequence Diagram " + (this.index + 1) + ": " + this.sd.getName() + "\n\n");
		printInSequence(this.sd, 0);
		System.out.print("\n\n");
	}

	public void printInSequence(Fragment fragment, int indent) {

		for (int i = 0; i < indent; i++)
			System.out.print("\t");
		System.out.println("Lifelines:");
		for (Lifeline l : fragment.getLifelines()) {
			for (int i = 0; i < indent; i++)
				System.out.print("\t");
			System.out.println(l.getName());
			// l.print_nodes();
		}
		System.out.println();

		for (int i = 0; i < indent; i++)
			System.out.print("\t");
		System.out.println("Nodes:");
		for (Node n : fragment.getNodes()) {
			for (int i = 0; i < indent; i++)
				System.out.print("\t");
			if (n.getClass().equals(Fragment.class)) {
				System.out.println(((Fragment) n).getName());
				// n.print();
				printInSequence((Fragment) n, (indent + 1));
			} else if (n.getClass().equals(Message.class)) {
				System.out.println("[" + ((Message) n).getName() + "]: "
						+ ((Message) n).getSender().getName() + "->"
						+ ((Message) n).getReceiver().getName() + " (" + ((Message) n).getType()
						+ ") p = " + n.getProb() + "; egy: " + n.getEnergy() + "; ex: "
						+ n.getExecTime());
				// n.print();
			}
		}
	}

	public SDReader(File xmlFile, int index) {
		this.index = index;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.coverage = new HashMap<Lifeline, ArrayList<String>>();
			this.doc = db.parse(xmlFile);
			this.doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public Fragment getSd() {
		return sd;
	}

	public void setSd(Fragment sd) {
		this.sd = sd;
	}

	public boolean hasNext() {
		return next;
	}

	public void retrieveLifelines() throws InvalidTagException, UnsupportedFragmentTypeException {
		NodeList nodes = this.doc.getElementsByTagName("ownedBehavior");
		if ((this.index + 1) == nodes.getLength())
			this.next = false;
		else
			this.next = true;
		org.w3c.dom.Node node = nodes.item(this.index);
		NodeList elements = node.getChildNodes();
		this.lifelines = new ArrayList<Lifeline>();
		for (int s = 0; s < elements.getLength(); s++) {
			if (elements.item(s).getNodeName().equals("lifeline")) {
				Lifeline tmp = new Lifeline(elements.item(s).getAttributes().getNamedItem("xmi:id")
						.getTextContent());
				ArrayList<String> coveredBy = new ArrayList<String>();
				tmp.setLink(elements.item(s).getAttributes().getNamedItem("represents")
						.getTextContent());
				if (elements.item(s).getAttributes().getNamedItem("name") != null) {
					tmp.setName(elements.item(s).getAttributes().getNamedItem("name")
							.getTextContent());
				}
				for (int j = 0; j < elements.item(s).getChildNodes().getLength(); j++) {
					if (elements.item(s).getChildNodes().item(j).getNodeName().equals("coveredBy")) {
						coveredBy.add(elements.item(s).getChildNodes().item(j).getAttributes()
								.getNamedItem("xmi:idref").getTextContent());
					}
				}
				this.coverage.put(tmp, coveredBy);
				this.lifelines.add(tmp);
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
		resetLifelinesByID();
	}

	private void resetLifelinesByID() {
		this.lifelinesByID = new HashMap<String, Lifeline>();
		for (Lifeline l : this.lifelines) {
			this.lifelinesByID.put(l.getId(), l);
		}
	}

	/*
	 * public void initOwnedFragmentLifelines() { for (Lifeline l :
	 * this.lifelines) { l.initOwnedFragment(); } }
	 */

	public void retrieveMessages() throws InvalidTagException {
		org.w3c.dom.Node node = this.doc.getElementsByTagName("ownedBehavior").item(this.index);
		NodeList elements = node.getChildNodes();

		this.messages = new ArrayList<Message>();
		for (int s = 0; s < elements.getLength(); s++) {
			if ((elements.item(s).getNodeValue() == null)
					&& (elements.item(s).getNodeName() == "message")) {
				Message message = new Message(elements.item(s).getAttributes()
						.getNamedItem("xmi:id").getTextContent());
				if (elements.item(s).getAttributes().getNamedItem("name") != null) {
					message.setName(elements.item(s).getAttributes().getNamedItem("name")
							.getTextContent().replace('\n', ' '));
				}
				for (Lifeline l : this.lifelines) {
					if ((this.coverage.get(l)).contains(elements.item(s).getAttributes()
							.getNamedItem("sendEvent").getTextContent())) {
						message.setSender(l);
					}
					if ((this.coverage.get(l)).contains(elements.item(s).getAttributes()
							.getNamedItem("receiveEvent").getTextContent())) {
						message.setReceiver(l);
					}
				}
				if (elements.item(s).getAttributes().getNamedItem("messageSort") != null) {
					if (elements.item(s).getAttributes().getNamedItem("messageSort")
							.getTextContent().equals("asynchCall")) {
						message.setType(MessageType.assynchronous);
					} else if (elements.item(s).getAttributes().getNamedItem("messageSort")
							.getTextContent().equals("reply")) {
						message.setType(MessageType.reply);
					}
				} else {
					message.setType(MessageType.synchronous);
				}

				retreiveProbEnergyTime(message);
				this.messages.add(message);
			}
		}
		resetMessagesByID();
	}

	private void resetMessagesByID() {
		this.messagesByID = new HashMap<String, Message>();
		for (Message m : this.messages) {
			this.messagesByID.put(m.getId(), m);
		}
	}

	private Float parseTag(String tagValue, String tagName) throws InvalidTagException {
		if (tagValue == "") {
			throw new InvalidTagException("Tag " + tagName + " is missing!", tagName);
		}
		Float parsedValue;
		try {
			parsedValue = Float.valueOf(tagValue);
		} catch (NumberFormatException e) {
			throw new InvalidTagException("Tag \"" + tagValue + "\" is not a float number!",
					tagName);
		}
		return parsedValue;
	}

	private void retreiveProbEnergyTime(Node n) throws InvalidTagException {
		retreiveProbEnergyTimeHelper(this.doc.getElementsByTagName("GQAM:GaStep"), n);

		retreiveProbEnergyTimeHelper(this.doc.getElementsByTagName("PAM:PaStep"), n);

		retreiveProbEnergyTimeHelper(this.doc.getElementsByTagName("GRM:ResourceUsage"), n);

		retreiveProbEnergyTimeHelper(this.doc.getElementsByTagName("PAM:PaCommStep"), n);
	}

	private void retreiveProbEnergyTimeHelper(NodeList nodes, Node n) throws InvalidTagException {
		for (int k = 0; k < nodes.getLength(); k++) {
			org.w3c.dom.Node tmp;
			if (nodes.item(k).getAttributes().getNamedItem("base_NamedElement").getTextContent()
					.equals(n.getId())) {
				if (nodes.item(k).getAttributes().getNamedItem("prob") != null) {
					n.setProb(parseTag(
							nodes.item(k).getAttributes().getNamedItem("prob").getTextContent(),
							"prob").floatValue());
				}
				if (nodes.item(k).hasChildNodes()) {
					for (int i = 0; i < nodes.item(k).getChildNodes().getLength(); i++) {
						tmp = nodes.item(k).getChildNodes().item(i);
						if ((tmp.getNodeName() != null) && (tmp.getNodeName().equals("energy"))) {
							n.setEnergy(parseTag(tmp.getTextContent(), "energy").floatValue());
						}
						if ((tmp.getNodeName() != null) && (tmp.getNodeName().equals("execTime"))) {
							n.setExecTime(parseTag(tmp.getTextContent(), "execTime").floatValue());
						}
					}
				}
			}
		}
	}

	public void traceDiagram() throws UnsupportedFragmentTypeException, InvalidTagException {

		org.w3c.dom.Node n = this.doc.getElementsByTagName("ownedBehavior").item(this.index);
		if (n.getAttributes().getNamedItem("name") != null) {
			this.sd = new Fragment(n.getAttributes().getNamedItem("xmi:id").getTextContent(), n
					.getAttributes().getNamedItem("name").getTextContent());
		} else {
			this.sd = new Fragment(n.getAttributes().getNamedItem("xmi:id").getTextContent(), null);
		}
		this.sd.setLifelines(this.lifelines);

		NodeList elements = n.getChildNodes();
		for (int i = 0; i < elements.getLength(); i++) {
			org.w3c.dom.Node node = elements.item(i);
			if (node.getNodeName().equals("fragment")) {
				if (node.getAttributes().getNamedItem("xmi:type").getTextContent()
						.equals("uml:MessageOccurrenceSpecification")) {
					String msgID = node.getAttributes().getNamedItem("message").getTextContent();
					this.sd.addNode(this.messagesByID.get(msgID));
					i += 2;
				} else if (node.getAttributes().getNamedItem("xmi:type").getTextContent()
						.equals("uml:CombinedFragment")) {

					Fragment fragment;
					if (node.getAttributes().getNamedItem("name") != null) {
						fragment = new Fragment(node.getAttributes().getNamedItem("xmi:id")
								.getTextContent(), node.getAttributes().getNamedItem("name")
								.getTextContent());
					} else {
						fragment = new Fragment(node.getAttributes().getNamedItem("xmi:id")
								.getTextContent(), null);
					}
					
					String fType = node.getAttributes().getNamedItem("interactionOperator").getTextContent();
					String guard = "";
					NodeList childNodes = node.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						if (childNodes.item(j).getNodeName().equals("operand")) {
							NodeList operandChildNodes = childNodes.item(j).getChildNodes();
							for (int k = 0; k < operandChildNodes.getLength(); k++) {
								org.w3c.dom.Node guardNode = operandChildNodes.item(k);
								if (guardNode.getNodeName().equals("guard")) {
									NodeList guardNodeChilds = guardNode.getChildNodes();
									for (int l = 0; l < guardNodeChilds.getLength(); l++) {
										if (guardNodeChilds.item(l).getNodeName().equals("specification")) {
											guard = guardNodeChilds.item(l).getAttributes().getNamedItem("value").getTextContent();
										}
									}
								}
							}
						}
					}
					fragment.setGuard(guard);
					fragment.setType(fType);
					retreiveProbEnergyTime(fragment);
					
					this.sd.addNode(fragment);
					traceFragment(fragment, node);
				}
			}
		}
	}

	public void traceFragment(Fragment fragment, org.w3c.dom.Node node)
			throws UnsupportedFragmentTypeException, InvalidTagException {

		NodeList fragElements = node.getChildNodes();
		for (int j = 0; j < fragElements.getLength(); j++) {
			if (fragElements.item(j).getNodeName().equals("covered")) {
				fragment.addLifeline(this.lifelinesByID.get(fragElements.item(j).getAttributes()
						.getNamedItem("xmi:idref").getTextContent()));
			} else if (fragElements.item(j).getNodeName().equals("operand")) {
				NodeList fragNodes = fragElements.item(j).getChildNodes();
				for (int k = 0; k < fragNodes.getLength(); k++) {
					if (fragNodes.item(k).getNodeName().equals("fragment")) {
						if (fragNodes.item(k).getAttributes().getNamedItem("xmi:type")
								.getTextContent().equals("uml:MessageOccurrenceSpecification")) {

							String msgID = fragNodes.item(k).getAttributes()
									.getNamedItem("message").getTextContent();
							fragment.addNode(this.messagesByID.get(msgID));
							k += 2;

						} else if (fragNodes.item(k).getAttributes().getNamedItem("xmi:type")
								.getTextContent().equals("uml:CombinedFragment")) {

							Fragment newFragment;
							if (fragNodes.item(k).getAttributes().getNamedItem("name") != null) {
								newFragment = new Fragment(fragNodes.item(k).getAttributes()
										.getNamedItem("xmi:id").getTextContent(), fragNodes.item(k)
										.getAttributes().getNamedItem("name").getTextContent());
							} else {
								newFragment = new Fragment(fragNodes.item(k).getAttributes()
										.getNamedItem("xmi:id").getTextContent(), null);
							}

							if (fragNodes.item(k).getAttributes()
									.getNamedItem("interactionOperator").getTextContent()
									.equals("opt")) {
								newFragment.setType(FragmentType.optional);
							} else {
								throw new UnsupportedFragmentTypeException("Type "
										+ fragNodes.item(k).getAttributes()
												.getNamedItem("interactionOperator")
												.getTextContent()
										+ " of Fragment is not supported!");
							}
							retreiveProbEnergyTime(newFragment);
							fragment.addNode(newFragment);
							traceFragment(newFragment, fragNodes.item(k));
						}
					} else if (fragNodes.item(k).getNodeName().equals("guard")) {
						NodeList guards = fragNodes.item(k).getChildNodes();
						for (int l = 0; l < guards.getLength(); l++) {
							if (guards.item(l).getNodeName().equals("specification")) {
								fragment.setOperandName(guards.item(l).getAttributes()
										.getNamedItem("value").getTextContent());

								break;
							}
						}
					}
				}
			}
		}
	}
}
