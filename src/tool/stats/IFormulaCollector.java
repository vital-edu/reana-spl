package tool.stats;

import java.io.PrintStream;

import tool.RDGNode;

/**
 * Collects formulas in order to generate stats.
 * @author thiago
 *
 */
public interface IFormulaCollector {

    public void collectFormula(RDGNode node, String formula);
    public long getMinFormulaSize();
    public long getMaxFormulaSize();
    public long getSizesSum();

    public void printStats(PrintStream out);

}
