package tool.stats;

public class NoopFormulaCollector implements IFormulaCollector {

    @Override
    public void collectFormula(String formula) {
        // No-op
    }

    @Override
    public long getMinFormulaSize() {
        // No-op
        return 0;
    }

    @Override
    public long getMaxFormulaSize() {
        // No-op
        return 0;
    }

    @Override
    public void printStats() {
        // No-op
    }

}
