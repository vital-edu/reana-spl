package FeatureFamilyBasedAnalysisTool;

import java.util.HashSet;
import java.util.Iterator;

public class FeatureModel {

	private HashSet<Feature> features; 
	
	public void addFeatures(HashSet<Feature> temp) {
		this.features = temp;		
	}
	
	
	public HashSet<Feature> getFeatures() {
		return (HashSet<Feature>) features.clone(); 
	}
	
	
	@Override
	public String toString() {
		System.out.println("Feature Model:");
		System.out.println("--------------");
		Iterator<Feature> it = features.iterator();
		while (it.hasNext()) {
			Feature f = it.next();
			System.out.println(f.getName());
		}
		return super.toString();
	}

}
