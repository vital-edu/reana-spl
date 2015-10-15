package tool.stats;

public class FormulaCollector implements IFormulaCollector {

    private long count = 0;
    private long minSize = Long.MAX_VALUE;
    private long maxSize = 0;

    @Override
    public void collectFormula(String formula) {
        count++;
        long formulaSize = formula.length();
        minSize = Math.min(minSize, formulaSize);
        maxSize = Math.max(maxSize, formulaSize);
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
    public void printStats() {
        System.out.println("Maximum formula size: " + maxSize);
        System.out.println("Minimum formula size: " + minSize);
        System.out.println("Number of formulae: " + count);
    }

}
