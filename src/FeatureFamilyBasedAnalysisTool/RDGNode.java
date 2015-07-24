package FeatureFamilyBasedAnalysisTool;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;


public class RDGNode {

	
	//This attribute is used to store the BDD for the RDG node
	private BDD bdd; 
	
	//This attribute is used to store the FDTMC for the RDG node. 
	private FDTMC fdtmc;  
	
	
	//This attribute is used to store the features which the RDG node depends on. 
	private Collection <Feature> featureDependencies;  
	//This attribute is used to store the evaluations for each valid partial configuration of the RDG node. 
	private Collection <Evaluation> evaluations; 
	
	public RDGNode() {
		this.featureDependencies = new HashSet<Feature>();  //the set of Feature Dependencies is empty
		this.evaluations = new HashSet<Evaluation>();  //the set of Evaluations is empty
	}
	
	
	public RDGNode(Collection <Feature> featureDependencies, Collection <Evaluation> evaluations) {
		this();
		this.featureDependencies = featureDependencies; 
		this.evaluations = evaluations;
	}


	public Collection <Evaluation> getEvaluations() {
		return this.evaluations;		
	}


	public Collection <Feature> getFeatureDependencies() {
		return this.featureDependencies;
	}


	public boolean addFeatureDependency(Feature feature) {
		return this.featureDependencies.add(feature);
	}


	public boolean removeFeatureDependency(Feature feature) {
		return this.featureDependencies.remove(feature);
	}


	/**
	 * This function is used to add an evaluation to a RDG node if there's no evaluation with the same partial configuration in the set. In case there's an evaluation 
	 * with the same partial configuration in the set, it will remains and no changes will occur at the evaluations set.
	 * @param ev
	 * @return
	 */
	public boolean addEvaluation(Evaluation ev) {
		Iterator<Evaluation> it = evaluations.iterator(); 
		
		boolean isEvaluationPresent = false; 
		while (it.hasNext() && !isEvaluationPresent) {
			Evaluation temp = (Evaluation) it.next();
			if (temp.getFeatures().equals(ev.getFeatures()))
				isEvaluationPresent = true;
		}
		
		if (!isEvaluationPresent)
			return this.evaluations.add(ev);
		else
			return false; 
	}

	
	
	/**
	 * This function must be used to delete an evaluation from a RDG node. The exclusion must occur based on the partial configurations, i.e., given an evaluation as input, if 
	 * there is an evaluation with the same partial configuration (it's not necessary having the same reliability value), it must be deleted. In case it was possible to delete 
	 * an evaluation, this function will return TRUE, otherwise it will return FALSE. 
	 * @param ev
	 * @return
	 */
	public boolean removeEvaluation(Evaluation ev) {
		Iterator<Evaluation> it = evaluations.iterator();
		
		boolean isEvaluationPresent = false; 
		while (it.hasNext() && !isEvaluationPresent) {
			Evaluation temp = it.next(); 
			if (temp.getFeatures().equals(ev.getFeatures()))
				isEvaluationPresent = true; 
		}
		
		if (isEvaluationPresent){
			it.remove();
			return true; 
		}
		else
			return false;
	}
	
	
	
	public void familyBasedAnalysis() {
		
	}
	
	
	public String FamilyBasedAnalysisPrime() {
		return StubFamilyBasedAnalysisPrime.evaluateFDTMC(this.fdtmc);
	}
	
	
}
