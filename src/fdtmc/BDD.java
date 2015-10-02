package fdtmc;

import java.awt.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class BDD {

	private static FeatureModel featureModel = new FeatureModel();
	private FeatureModel projection; 
	private HashSet<Feature> dependantFeatures;
	private HashSet<PartialConfiguration> partialConfigurations; 
	
	public BDD() {
		if (featureModel == null)
			featureModel = new FeatureModel();
	}
	
	
	public BDD (FeatureModel featureModel) {
		this.featureModel = featureModel;
	}

	public FeatureModel getFeatureModel() {
		return this.featureModel;
	}


	public void addSetOfDependantFeatures(HashSet<Feature> dependantFeatures) {
		this.dependantFeatures = dependantFeatures;
	}


	public HashSet<Feature> getDependantFeatures() {
		return this.dependantFeatures;
	}


	/**
	 * This function is used to create the projection of a set of Features over the 
	 * FeatureModel. It receives the set of features as input and store the resulting
	 * feature model in its attribute called projection.
	 * 
	 * @param features
	 * @return 
	 * 
	 * TODO ensure this function will create a new Feature Model for the projection, 
	 * with the set of features and rules comprising the set of dependant features.
	 */
	public FeatureModel createProjection(HashSet<Feature> features) {
		this.dependantFeatures = features; 
		HashSet<Feature> temp = featureModel.getFeatures(); 
		temp.retainAll(features);
		
		projection = new FeatureModel(); 
		projection.addFeatures(temp);
		
		return projection;		
	}


	public void setFeatureModel(FeatureModel fm) {
		this.featureModel = fm;		
	}


	public FeatureModel getProjection() {
		return projection;
	}


	/**
	 * This function is used to return the set of valid partial configurations
	 * @return
	 */
	public HashSet<PartialConfiguration> getValidPartialConfigurations() {
		partialConfigurations = new HashSet<PartialConfiguration>();
//		System.out.println("dependantFeatures empty?" + (dependantFeatures.isEmpty() ? "yes":"no"));
		if (dependantFeatures.isEmpty())
			return partialConfigurations; 
		else {
//			System.out.println("entrei no else");
			partialConfigurations = StubValidPartialConfigurations.getValidPartialConfigurations(dependantFeatures);
			//System.out.println(partialConfigurations);
		}
//		System.out.println(printHashSet(dependantFeatures, "dependantFeatures"));
//		System.out.println(printHashSet(partialConfigurations, "partialConfigurations"));
		return partialConfigurations;
	} 
	
	
	private static String printHashSet(HashSet <Feature> h, String name) {
		String resposta = new String(); 
		resposta += "HashSet: " + name + '\n'; 
		Iterator<Feature> it = h.iterator(); 
		while (it.hasNext()) {
			resposta += ((Feature)it.next()).getName() + '\n';
		}
		return resposta;
	}
	
	
}
