package tool.stats;

public class WallClockStopWatch {

    private long startTime;
    private long cumulativeTime = 0;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long stop() {
        long delta = System.currentTimeMillis() - startTime;
        cumulativeTime += delta;
        return delta;
    }

    public long getCumulativeTime() {
        return cumulativeTime;
    }
}
