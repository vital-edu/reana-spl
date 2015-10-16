package tool.stats;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryCollector implements IMemoryCollector {

    private Map<String, Long> snapshots;

    public MemoryCollector() {
        // LinkedHashMap provides for ordered iteration of the snapshots.
        snapshots = new LinkedHashMap<String, Long>();
    }

    @Override
    public void takeSnapshot(String name) {
        Runtime runtime = Runtime.getRuntime();
        long snapshot = runtime.totalMemory() - runtime.freeMemory();
        snapshots.put(name, snapshot);
    }

    @Override
    public void printStats() {
        for (Map.Entry<String, Long> snapshot: snapshots.entrySet()) {
            double valueInMegabytes = snapshot.getValue()/(1024.0*1024.0);
            System.out.println("Memory used "+snapshot.getKey()+": "+valueInMegabytes+" MB");
        }
    }

}
