package ui.stats;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import tool.RDGNode;
import tool.stats.IFormulaCollector;

public class FormulaCollector implements IFormulaCollector {

    private long count = 0;
    private long minSize = Long.MAX_VALUE;
    private long maxSize = 0;
    private long sizesSum = 0;
    private Map<RDGNode, String> formulae = new HashMap<RDGNode, String>();

    @Override
    public void collectFormula(RDGNode node, String formula) {
        count++;
        long formulaSize = formula.length();
        minSize = Math.min(minSize, formulaSize);
        maxSize = Math.max(maxSize, formulaSize);
        sizesSum += formulaSize;
        formulae.put(node, formula);
    }

    @Override
    public long getMinFormulaSize() {
        return minSize;
    }

    @Override
    public long getMaxFormulaSize() {
        return maxSize;
    }

    @Override
    public long getSizesSum() {
        return sizesSum;
    }

    @Override
    public void printStats(PrintStream out) {
        out.println("Maximum formula size: " + maxSize);
        out.println("Minimum formula size: " + minSize);
        out.println("Sum of formulae sizes: " + sizesSum);
        out.println("Number of formulae: " + count);
        for (Map.Entry<RDGNode, String> entry: formulae.entrySet()) {
            RDGNode node = entry.getKey();
            int numChildren = node.getDependencies().size();
            int height = node.getHeight();
            String formula = entry.getValue();
            int formulaSize = formula.length();
            out.println("    " + node.getId() + ": "
                        + numChildren + " children | height " + height + " | "
                        + formulaSize + " bytes | " + formula);
        }
    }

}
