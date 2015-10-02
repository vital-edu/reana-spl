package fdtmc;

import java.util.HashSet;
import java.util.Iterator;

public class Feature {
	private static HashSet<Feature> features = new HashSet<Feature>(); 
	
	private String name; 
	
	public static Feature getFeatureByName(String featureName) {
		Iterator<Feature> it = features.iterator(); 
		while (it.hasNext()) {
			Feature f = it.next();
			if (f.getName().equals(featureName)) {
				return f; 
			}
		}
		Feature f = new Feature(featureName);
		features.add(f);
		return f; 
	}
	
	private Feature() {
		// TODO Auto-generated constructor stub
	}
	
	private Feature(String name) {
		this();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}