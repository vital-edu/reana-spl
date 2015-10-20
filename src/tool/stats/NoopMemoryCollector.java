package tool.stats;

public class NoopMemoryCollector implements IMemoryCollector {

    @Override
    public void takeSnapshot(String name) {
        // No-op
    }

    @Override
    public void printStats() {
        // No-op
    }

}
