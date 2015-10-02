package fdtmc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class DependantFeatureEvaluation {

	Collection<Feature> partialConfiguration;
	HashMap<Feature, Double> featureReliability;
	
	public DependantFeatureEvaluation() {
		partialConfiguration = new HashSet<Feature>(); 
		featureReliability = new HashMap<Feature, Double>();
	}
	
	public DependantFeatureEvaluation(Collection<Feature> partialConfiguration, HashMap<Feature, Double> featureReliability) {
		this();
		this.partialConfiguration = partialConfiguration;
		this.featureReliability = featureReliability;
	}

	public Collection<Feature> getPartialConfiguration() {
		return partialConfiguration;
	}

	public HashMap<Feature, Double> getFeatureReliability() {
		return featureReliability;
	}

	public boolean addFeature(Feature feature) {
		return partialConfiguration.add(feature);
	}

	public boolean addFeature(Collection<Feature> pc) {
		return partialConfiguration.addAll(pc);
		
	}

	
	public boolean addFeature(PartialConfiguration pc) {
		return partialConfiguration.addAll(pc.getFeatures());
	}
	
	
	public Double addFeatureReliabilityValue(Feature feature, double value) {
		return featureReliability.put(feature, value);
	}

	
	public void addFeatureReliabilityValue(
			HashMap<Feature, Double> featureEvaluation) {
		featureReliability.putAll(featureEvaluation);
	}

}
