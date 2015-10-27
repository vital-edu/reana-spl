package tool.stats;

import java.io.PrintStream;

import tool.RDGNode;

public class NoopFormulaCollector implements IFormulaCollector {

    @Override
    public void collectFormula(RDGNode node, String formula) {
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
    public void printStats(PrintStream out) {
        // No-op
    }

    @Override
    public long getSizesSum() {
        // No-op
        return 0;
    }

}
