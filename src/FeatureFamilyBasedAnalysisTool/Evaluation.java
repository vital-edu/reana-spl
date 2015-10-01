package FeatureFamilyBasedAnalysisTool;
import java.util.Collection;
import java.util.HashSet;


public class Evaluation {

	private Collection <Feature> features; 
	private double reliability;
	
	
	public Evaluation() {
		this.features = new <Feature> HashSet<Feature>();
		this.reliability = 0; 
	}
	
	
	public Evaluation(Collection<Feature> features, double reliability) {
		this(); 
		this.features.addAll(features); 
		this.reliability = reliability;
	}


	public boolean addFeature(Feature feature) {
		return features.add(feature);
	}


	public Collection<Feature> getFeatures() {
		return this.features;
	}


	public boolean remove(Feature feature) {
		return this.features.remove(feature);
	}
	
	
	public void setRealiability (double reliability) {
		this.reliability = reliability;
	}
	
	
	public double getReliability () {
		return this.reliability; 
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Evaluation ev = (Evaluation) obj;
		if (this.getFeatures().equals(ev.getFeatures()) && (this.getReliability() == ev.getReliability()))
			return true;
		return super.equals(obj);
	}
}
