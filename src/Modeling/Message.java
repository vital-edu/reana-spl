package Modeling;

public class Message extends Node {
	private String name;
	private Lifeline sender;
	private Lifeline receiver;
	private MessageType type;
	
	public Message(String id) {
		super(id);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	private void newLoopIteration(Fragment f) {
		this.nextLoopIteration = new Message(newID(f));
		((Message) this.nextLoopIteration).setProb(getProb());
		((Message) this.nextLoopIteration).setName(getName());
		((Message) this.nextLoopIteration).setReceiver(getReceiver());
		((Message) this.nextLoopIteration).setSender(getSender());
		((Message) this.nextLoopIteration).setType(getType());
		for (Node msg : this.loops) {
			if (msg.getId().equals(this.nextLoopIteration.getId())) {
				this.nextLoopIteration = msg;
				return;
			}
		}
		this.loops.add((Message) this.nextLoopIteration);
	}

	public Message getNextLoopIteration(Fragment f) {
		newLoopIteration(f);
		return (Message) this.nextLoopIteration;
	}

	public Lifeline getSender() {
		return this.sender;
	}

	public void setSender(Lifeline sender) {
		this.sender = sender;
	}

	public void setReceiver(Lifeline receiver) {
		this.receiver = receiver;
	}

	public Lifeline getReceiver() {
		return this.receiver;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public void print() {
		super.print();
	}
}
