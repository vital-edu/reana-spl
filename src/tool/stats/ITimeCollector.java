package tool.stats;

/**
 * Interface for a time stats collector for the ReAna tool.
 * @author thiago
 *
 */
public interface ITimeCollector {

    public void startParsingTimer();
    public void stopParsingTimer();
    /**
     * Wall-clock time used during parsing.
     * @return
     */
    public long getParsingTime();

    public void startFeatureBasedTimer();
    public void stopFeatureBasedTimer();
    /**
     * Wall-clock time used during feature-based analysis.
     * @return
     */
    public long getFeatureBasedTime();

    public void startFamilyBasedTimer();
    public void stopFamilyBasedTimer();
    /**
     * Wall-clock time used during family-based analysis.
     * @return
     */
    public long getFamilyBasedTime();

    public void printStats();

}
