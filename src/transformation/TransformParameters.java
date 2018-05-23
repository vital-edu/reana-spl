package transformation;

import fdtmc.FDTMC;
import fdtmc.State;
import parsing.sequencediagrams.Fragment;
import tool.RDGNode;

public class TransformParameters {
	private FDTMC fdtmc;
	private Fragment fragment;
	private State source;
	private State target;
	private State error;
	private RDGNode currentRdgNode;

	public TransformParameters(FDTMC fdtmc, Fragment fragment, State source, State target, State error,
			RDGNode currentRdgNode) {
		this.fdtmc = fdtmc;
		this.fragment = fragment;
		this.source = source;
		this.target = target;
		this.error = error;
		this.currentRdgNode = currentRdgNode;
	}

	public FDTMC getFdtmc() {
		return fdtmc;
	}

	public void setFdtmc(FDTMC fdtmc) {
		this.fdtmc = fdtmc;
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public State getSource() {
		return source;
	}

	public void setSource(State source) {
		this.source = source;
	}

	public State getTarget() {
		return target;
	}

	public void setTarget(State target) {
		this.target = target;
	}

	public State getError() {
		return error;
	}

	public void setError(State error) {
		this.error = error;
	}

	public RDGNode getCurrentRdgNode() {
		return currentRdgNode;
	}

	public void setCurrentRdgNode(RDGNode currentRdgNode) {
		this.currentRdgNode = currentRdgNode;
	}
}