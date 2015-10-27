package tool;


public interface IReliabilityAnalysisResults {

    /**
     * Gets the specific result for the given configuration or zero if the
     * configuration has no associated result or is invalid.
     *
     * @param configuration
     * @return
     * @throws UnknownFeatureException in case the configuration relies on an
     *      unknown feature.
     */
    public Double getResult(String[] configuration) throws UnknownFeatureException;

}
