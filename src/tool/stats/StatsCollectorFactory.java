package tool.stats;

public class StatsCollectorFactory {

    private boolean collectionEnabled = false;

    public StatsCollectorFactory(boolean collectionEnabled) {
        this.collectionEnabled = collectionEnabled;
    }

    public ITimeCollector createTimeCollector() {
        if (collectionEnabled) {
            return new TimeCollector();
        } else {
            return new NoopTimeCollector();
        }
    }

    public IFormulaCollector createFormulaCollector() {
        if (collectionEnabled) {
            return new FormulaCollector();
        } else {
            return new NoopFormulaCollector();
        }
    }

    public IMemoryCollector createMemoryCollector() {
        if (collectionEnabled) {
            return new MemoryCollector();
        } else {
            return new NoopMemoryCollector();
        }
    }

}
