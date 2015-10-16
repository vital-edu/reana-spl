package tool.stats;

public interface IMemoryCollector {

    public void takeSnapshot(String name);

    public void printStats();

}
