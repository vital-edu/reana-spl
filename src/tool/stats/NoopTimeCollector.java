package tool.stats;

public class NoopTimeCollector implements ITimeCollector {

    @Override
    public void startParsingTimer() {
        // No-op
    }

    @Override
    public void stopParsingTimer() {
        // No-op
    }

    @Override
    public long getParsingTime() {
        // No-op
        return 0;
    }

    @Override
    public void startFeatureBasedTimer() {
        // No-op
    }

    @Override
    public void stopFeatureBasedTimer() {
        // No-op
    }

    @Override
    public long getFeatureBasedTime() {
        // No-op
        return 0;
    }

    @Override
    public void startFamilyBasedTimer() {
        // No-op

    }

    @Override
    public void stopFamilyBasedTimer() {
        // No-op
    }

    @Override
    public long getFamilyBasedTime() {
        // No-op
        return 0;
    }

    @Override
    public void printStats() {
        // No-op
    }

}
