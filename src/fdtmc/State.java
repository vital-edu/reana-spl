package fdtmc;

public class State {

	private String variableName;
	private int index;
	private String label;

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIndex() {
		return index;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getLabel() {
		return label;
	}

}
