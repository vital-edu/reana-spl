package tool;

import jadd.ADD;
import jadd.UnrecognizedVariableException;

public class ADDReliabilityResults implements IReliabilityAnalysisResults {

    private ADD results;

    public ADDReliabilityResults(ADD results) {
        this.results = results;
    }

    @Override
    public Double getResult(String[] configuration) throws UnknownFeatureException {
        try {
            return results.eval(configuration);
        } catch (UnrecognizedVariableException e) {
            throw new UnknownFeatureException(e.getVariableName());
        }
    }

}
