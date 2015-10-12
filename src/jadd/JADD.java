package jadd;

import java.util.Map;

import org.bridj.Pointer;

import bigcudd.BigcuddLibrary;
import bigcudd.DdNode;

/**
 * Interface to basic ADD operations.
 *
 * @author thiago
 *
 */
public class JADD {

    private Pointer<BigcuddLibrary.DdManager> dd;
    private VariableStore variableStore = new VariableStore();

    public JADD() {
        dd = BigcuddLibrary.Cudd_Init(0,
                                      0,
                                      BigcuddLibrary.CUDD_UNIQUE_SLOTS,
                                      BigcuddLibrary.CUDD_CACHE_SLOTS,
                                      0);
    }

    public ADD makeConstant(double constant) {
        return new ADD(dd,
                       BigcuddLibrary.Cudd_addConst(dd,  constant),
                       variableStore);
    }

    public ADD getVariable(String varName) {
        if (variableStore.contains(varName)) {
            return variableStore.get(varName);
        } else {
            Pointer<DdNode> var = BigcuddLibrary.Cudd_addNewVar(dd);
            ADD varADD = new ADD(dd, var, variableStore);
            variableStore.put(var.get().index(), varName, varADD);
            return varADD;
        }
    }

    public void dumpDot(String[] functionNames, ADD[] functions, String fileName) {
        Pointer<?> output = CUtils.fopen(fileName, CUtils.ACCESS_WRITE);

        @SuppressWarnings("unchecked")
        Pointer<DdNode>[] nodes = (Pointer<DdNode>[]) new Pointer[functions.length];
        int i = 0;
        for (ADD function : functions) {
            nodes[i] = function.getUnderlyingNode();
            i++;
        }

        String[] orderedVariableNames = variableStore.getOrderedNames();
        BigcuddLibrary.Cudd_DumpDot(dd,
                                    functions.length,
                                    Pointer.pointerToPointers(nodes),
                                    Pointer.pointerToCStrings(orderedVariableNames),
                                    Pointer.pointerToCStrings(functionNames),
                                    output);

        CUtils.fclose(output);
    }

    public void dumpDot(Map<String, ADD> functions, String fileName) {
        String[] functionNames = new String[functions.size()];
        ADD[] nodes = new ADD[functions.size()];

        // TODO Do Map.values() and Map.keys() always return values and respective keys in the same order?
        // If so, we can avoid explicit iteration by using only these methods.
        int i = 0;
        for (Map.Entry<String, ADD> function: functions.entrySet()) {
            functionNames[i] = function.getKey();
            nodes[i] = function.getValue();
            i++;
        }
        dumpDot(functionNames, nodes, fileName);
    }

    public void dumpDot(String functionName, ADD function, String fileName) {
        dumpDot(new String[]{functionName},
                new ADD[]{function},
                fileName);
    }

}
