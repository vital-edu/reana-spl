package tool.stats;

public class TimeCollector implements ITimeCollector {

    private WallClockStopWatch parsingTimer;
    private WallClockStopWatch featureBasedTimer;
    private WallClockStopWatch familyBasedTimer;

    public TimeCollector() {
        parsingTimer = new WallClockStopWatch();
        featureBasedTimer = new WallClockStopWatch();
        familyBasedTimer = new WallClockStopWatch();
    }

    @Override
    public void startParsingTimer() {
        parsingTimer.start();
    }

    @Override
    public void stopParsingTimer() {
        parsingTimer.stop();
    }

    @Override
    public void startFeatureBasedTimer() {
        featureBasedTimer.start();
    }

    @Override
    public void stopFeatureBasedTimer() {
        featureBasedTimer.stop();
    }

    @Override
    public void startFamilyBasedTimer() {
        familyBasedTimer.start();
    }

    @Override
    public void stopFamilyBasedTimer() {
        familyBasedTimer.stop();
    }

    @Override
    public long getParsingTime() {
        return parsingTimer.getCumulativeTime();
    }

    @Override
    public long getFeatureBasedTime() {
        return featureBasedTimer.getCumulativeTime();
    }

    @Override
    public long getFamilyBasedTime() {
        return familyBasedTimer.getCumulativeTime();
    }

    @Override
    public void printStats() {
        System.out.println("Parsing time: " + getParsingTime()/1E+6 + " ms");
        System.out.println("Feature-based time: " + getFeatureBasedTime()/1E+6 + " ms");
        System.out.println("Family-based time: " + getFamilyBasedTime()/1E+6 + " ms");
    }

}
