package tool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapBasedReliabilityResults implements IReliabilityAnalysisResults {

    private Map<Set<String>, Double> results;
    private Set<String> features;

    public MapBasedReliabilityResults() {
        this.results = new HashMap<Set<String>, Double>();
        this.features = new HashSet<String>();
    }

    @Override
    public Double getResult(String[] configuration) throws UnknownFeatureException {
        Set<String> configurationAsSet = new HashSet<String>(Arrays.asList(configuration));
        if (results.containsKey(configurationAsSet)) {
            return results.get(configurationAsSet);
        } else if (hasUnknownFeature(configurationAsSet)) {
            throw new UnknownFeatureException(configuration.toString());
        }
        return 0.0;
    }

    public void putResult(List<String> configuration, Double value) {
        Set<String> configurationAsSet = new HashSet<String>(configuration);
        results.put(configurationAsSet, value);
        features.addAll(configurationAsSet);

    }

    private boolean hasUnknownFeature(Set<String> configuration) {
        return features.containsAll(configuration);
    }

}
