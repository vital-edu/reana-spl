package FeatureFamilyBasedAnalysisTool;

public class Transition {

	private String probability;
	private State source, target;

	public Transition(State source, State target, String probability) {
		this.source = source;
		this.target = target;
		this.probability = probability;
	}

	public String getProbability() {
		return probability;
	}

	public State getSource() {
		return source;
	}

	public State getTarget() {
		return target;
	}
}
