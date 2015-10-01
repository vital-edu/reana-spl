package FeatureFamilyBasedAnalysisTool;

import java.util.HashSet;
import java.util.Iterator;

public class PartialConfiguration {
	private HashSet <Feature> features;
	
	public PartialConfiguration() {
		features = new HashSet<Feature>();
	}
	
	
	public PartialConfiguration(HashSet<Feature> features) {
		this.features = features;
	}


	public void addFeature(Feature f) {
		features.add(f);
	}
	
	
	public HashSet<Feature> getFeatures() {
		return features;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String resposta = new String();
		resposta += "PartialConfiguration id= " + this.hashCode() + '\n';
		resposta += "-------------------------------------\n";
		Iterator<Feature> it = features.iterator(); 
		while (it.hasNext()) 
			resposta += ((Feature)it.next()).getName() + '\n';
		return resposta;
	}


	public boolean remove(PartialConfiguration pc) {
		return features.removeAll(pc.getFeatures());
	}
}
