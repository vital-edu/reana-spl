package tool;
import jadd.ADD;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fdtmc.BDD;
import fdtmc.DependantFeatureEvaluation;
import fdtmc.Evaluation;
import fdtmc.FDTMC;
import fdtmc.FDTMCStub;
import fdtmc.Feature;
import fdtmc.FeatureModel;
import fdtmc.Formula;
import fdtmc.PartialConfiguration;
import fdtmc.StubFamilyBasedAnalysisPrime;
import test.ReliabilityEvaluationTest;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class RDGNode {

	//This reference is used to store all the RDGnodes created during the evaluation
	private static HashMap<Feature, RDGNode> rdgNodes = new HashMap<Feature, RDGNode>();


	//This attribute is used to store the BDD for the RDG node
	private BDD bdd;

	//This attribute is used to store the FDTMC for the RDG node.
	private FDTMC fdtmc;

	//The node must have a feature or activity associated with.
	private Feature feature;


	//This attribute is used to store the features which the RDG node depends on.
	private Collection <Feature> featureDependencies;
	//This attribute is used to store the evaluations for each valid partial configuration of the RDG node.
	private Collection <Evaluation> evaluations;


	private String formula;

	private String id;
	private Collection<RDGNode> children;
    private ADD presenceCondition;




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


	/**
	 * This function implements the first step of FABE evaluation function: for a given feature, it will return its FDTMC.
	 */
	public void firstStep() {
		modeling();
	}

	/**
	 * This function implements the second step of FABE evaluation function.
	 * Its role is to discover the feature dependencies and compute the BDD (using features projection) for the RDG node.
	 * @param fmBSN
	 */

	public void secondStep(FeatureModel fmBSN) {
		bdd = new BDD(fmBSN);
//		System.out.println("#featureDependencies:" + featureDependencies.size());
//		System.out.println(featureDependencies);
		bdd.createProjection((HashSet<Feature>) featureDependencies);
//		System.out.println(bdd.getProjection().toString());
//		System.out.println(bdd.getProjection().getFeatures().isEmpty());
	}



	public void thirdStep() {
		if (fdtmc.getInterfaces().isEmpty()) {
			return;
		} else {
			fdtmc = analyseEvaluationEquality();
		}
	}

	/**
	 * this function will evaluate the FDTMC seeking for evaluations equality. In case the FDTMC has equal evaluations,
	 * sharing will happen and a new FDTMC (simpler than the original FDTMC) will be returned.
	 * @return
	 */
	private FDTMC analyseEvaluationEquality() {
		//to be implemented!
		return fdtmc;
	}

	/**
	 * This function's role is to create the FDTMC for a single feature/activity, represented at an RDG node.
	 * Currently it is being implemented by a stub. It should be removed when integrating Paula's work.
	 */
	private void modeling() {
		this.fdtmc = FDTMCStub.createFDTMC(feature);
	}




	public void fourthStep() {
		System.out.println("fdtmc: " + fdtmc);

		formula = StubFamilyBasedAnalysisPrime.evaluateFDTMC(fdtmc);
		System.out.println("formula: " + formula);

		if (this.featureDependencies.size() == 0) { //in case there's no feature dependencies (i.e., it's a basic RDG node)
			double reliabilityValue = Formula.evalFormula(this.formula);
			System.out.println("reliability value: " + reliabilityValue);
			Evaluation ev = new Evaluation(new HashSet<Feature>(), reliabilityValue);
			evaluations.add(ev);
		} else {
			/**
			 * At this point the algorithm must:
			 * a) get all valid partial configurations described in terms of the dependant features for the RDG node
			 * b) create a dependantFeature object by joining the evaluations for all dependant features present in the valid partial configuration
			 *    <{features}, [(feature, reliabilityValue)]>
			 * c) for each dependantFeatureEvaluation object describing a valid configuration, the algorithm exercises the formula. A new evaluation object is created
			 * and it consists of the valid configuration (coming from the dependantFeatureEvaluation object) and the reliability value (which is the relia-
			 * bility value resulting from formula evaluation).
			 */


			HashSet<PartialConfiguration> partialConfigurations = bdd.getValidPartialConfigurations();

			Iterator<PartialConfiguration> itPartialConfigurations = partialConfigurations.iterator();
			while (itPartialConfigurations.hasNext()) {
				PartialConfiguration pc = itPartialConfigurations.next();

				//Create the set of dependantFeatureEvaluations
				HashSet<DependantFeatureEvaluation> dependantFeatureEvaluations = new HashSet<DependantFeatureEvaluation>();
				Iterator<Feature> itFeatures = pc.getFeatures().iterator();
				//for each feature in partial configuration pc, we must get its evaluations (set of features and reliability value)
//				String config = new String();
				while(itFeatures.hasNext()) {
					Feature f = itFeatures.next();
//					System.out.println(f.getName());
//					config += f.getName() + " " ;
					RDGNode r = RDGNode.getNodeByFeature(f);
					dependantFeatureEvaluations = createDependantFeatureEvaluations(dependantFeatureEvaluations, r);

				}
				System.out.println("#dependantFeatureEvaluations: " + dependantFeatureEvaluations.size());

				HashSet<Feature> complementPC = new HashSet<Feature>(featureDependencies);
				complementPC.removeAll(pc.getFeatures());

				Iterator<DependantFeatureEvaluation> itDfe = dependantFeatureEvaluations.iterator();
				while (itDfe.hasNext()) {
					DependantFeatureEvaluation d = itDfe.next();

					Iterator<Feature> itComplementFeatures = complementPC.iterator();
					while (itComplementFeatures.hasNext()) {
						Feature f = itComplementFeatures.next();
						d.addFeatureReliabilityValue(f, 1);
					}
				}

				printDependantFeatureEvaluations(dependantFeatureEvaluations);


				//CONTINUAR DAQUI!
				//EXERCITAR A FORMULA PARA CADA CONFIGURACAO
				itDfe = dependantFeatureEvaluations.iterator();
				while (itDfe.hasNext()) {
					DependantFeatureEvaluation dfe = itDfe.next();
					double reliabilityValue = Formula.evalFormula(this.formula, dfe.getFeatureReliability());

					Evaluation ev = new Evaluation(new HashSet<Feature>(dfe.getPartialConfiguration()), reliabilityValue);
					evaluations.add(ev);
				}
			}
		}
		printEvaluations();
	}



	private void printEvaluations() {
		System.out.println("Evaluations of feature: " + getFeature().getName());

		Iterator<Evaluation> itEvaluations = evaluations.iterator();
		String out = new String();
		while (itEvaluations.hasNext()) {
			Evaluation e = itEvaluations.next();
			Iterator<Feature> itFeatures = e.getFeatures().iterator();

			out += "{ ";
			while (itFeatures.hasNext()) {
				Feature f = itFeatures.next();
				out += f.getName() + (itFeatures.hasNext() ? " , " : "");
			}
			out += " }";
			out += " : " + e.getReliability() + "\n";
		}
		System.out.println(out);
	}


	private void printDependantFeatureEvaluations(HashSet<DependantFeatureEvaluation> dependantFeatureEvaluations) {
		String out = new String();
//		out += "dependantFeatureEvaluation objectId = " + dependantFeatureEvaluations.toString() + "\n";
		out += "<{";
		Iterator<DependantFeatureEvaluation> itDfe = dependantFeatureEvaluations.iterator();
		while (itDfe.hasNext()) {
			DependantFeatureEvaluation temp = itDfe.next();
			Iterator<Feature> itFeature = temp.getPartialConfiguration().iterator();
			while (itFeature.hasNext()) {
				Feature f = itFeature.next();
				out += f.getName() + (itFeature.hasNext() ? " , " : "");
			}
			out += "},[";
			HashMap<Feature, Double> map = temp.getFeatureReliability();
			Set<Feature> setKeys = temp.getFeatureReliability().keySet();
			Iterator<Feature> itSetKeys = setKeys.iterator();
			while (itSetKeys.hasNext()) {
				Feature f = itSetKeys.next();
				out += "(" + f.getName() + "," + map.get(f).doubleValue() + ")" + (itSetKeys.hasNext() ? " , " : "");
			}
		}
		out += "]>";
		System.out.println(out);

	}


	private HashSet<DependantFeatureEvaluation> createDependantFeatureEvaluations(
			HashSet<DependantFeatureEvaluation> dependantFeatureEvaluations,
			RDGNode r) {
		HashSet<DependantFeatureEvaluation> answer = new HashSet<DependantFeatureEvaluation>();
		if (dependantFeatureEvaluations.isEmpty()) {
//			System.out.println("Entrei no if");
			System.out.println("Feature name: " + r.getFeature().getName());
			System.out.println("#Evaluation for r.: " + r.getEvaluations().size());
			Iterator<Evaluation> it = r.getEvaluations().iterator();
			while (it.hasNext()) {
				Evaluation e = it.next();
				DependantFeatureEvaluation dfe = new DependantFeatureEvaluation();
				dfe.addFeature(r.getFeature());
				dfe.addFeature(e.getFeatures());
				dfe.addFeatureReliabilityValue(r.getFeature(), e.getReliability());

				answer.add(dfe);
			}
		} else {  //TO BE TESTED!!!!
			System.out.println("Entrei no else");
			Iterator<DependantFeatureEvaluation> itDfe = dependantFeatureEvaluations.iterator();
			while (itDfe.hasNext()) {
				DependantFeatureEvaluation dfe = itDfe.next();

				Iterator<Evaluation> itEv = r.getEvaluations().iterator();
				while (itEv.hasNext()) {
					Evaluation e = itEv.next();
					DependantFeatureEvaluation newDfe = new DependantFeatureEvaluation();
					newDfe.addFeature(r.getFeature());
					newDfe.addFeature(e.getFeatures());
					newDfe.addFeatureReliabilityValue(r.getFeature(), e.getReliability());
					newDfe.addFeature(dfe.getPartialConfiguration());
					newDfe.addFeatureReliabilityValue(dfe.getFeatureReliability());
					answer.add(newDfe);
				}
			}
		}
		return answer;
	}


	private static RDGNode getNodeByFeature(Feature feature) {
		return rdgNodes.get(feature);
	}


	public void familyBasedAnalysis() {

	}


	public String FamilyBasedAnalysisPrime() {
		return StubFamilyBasedAnalysisPrime.evaluateFDTMC(this.fdtmc);
	}


	public void setFeature(Feature feature) {
		 this.feature = feature;
		 RDGNode.rdgNodes.put(feature, this);
	}


	public Feature getFeature() {
		return feature;
	}


	public FDTMC getFDTMC() {
		return this.fdtmc;
	}


	public BDD getBDD() {
		return this.bdd;
	}


	public boolean contains(Evaluation obj) {
		boolean answer = false;

		Iterator <Evaluation> it = this.evaluations.iterator();
		while (it.hasNext() && !answer) {
			Evaluation ev = it.next();
			Collection <Feature> c = ev.getFeatures();

			if (c.equals(obj.getFeatures()) && (ev.getReliability() == obj.getReliability()))
				answer = true;
		}
		return answer;
	}

    public Collection<RDGNode> getChildren() {
        return children;
    }

    public ADD getPresenceCondition() {
        return presenceCondition;
    }

    public String getId() {
        return id;
    }







}
