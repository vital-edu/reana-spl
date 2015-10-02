package jadd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class VariableStore {
    private Map<String, ADD> variables;
    private SortedMap<Short, String> variableNames;

    public VariableStore() {
        variables = new HashMap<String, ADD>();
        variableNames = new TreeMap<Short, String>();
    }

    public boolean contains(String varName) {
        return variables.containsKey(varName);
    }

    public ADD get(String varName) {
        return variables.get(varName);
    }

    public String getName(short varIndex) {
        return variableNames.get(varIndex);
    }

    public void put(short varIndex, String varName, ADD varADD) {
        variables.put(varName, varADD);
        variableNames.put(varIndex, varName);
    }

    public String[] getOrderedNames() {
        Collection<String> values = variableNames.values();
        return values.toArray(new String[values.size()]);
    }
}