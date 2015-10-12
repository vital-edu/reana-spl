package jadd;

import java.util.HashSet;
import java.util.Set;

import org.bridj.Pointer;

import bigcudd.BigcuddLibrary;
import bigcudd.BigcuddLibrary.Cudd_addApply_arg1_callback;
import bigcudd.BigcuddLibrary.DdManager;
import bigcudd.DdNode;

/**
 * ADD - constant, variable or function alike.
 * @author thiago
 *
 */
public class ADD {
    private static double FLOATING_POINT_PRECISION = 1E-14;

    private Pointer<DdNode> function;
    private Pointer<DdManager> dd;
    private VariableStore variableStore;

    ADD(Pointer<DdManager> dd, Pointer<DdNode> function, VariableStore variableStore) {
        this.dd = dd;
        this.function = function;
        this.variableStore = variableStore;
        BigcuddLibrary.Cudd_Ref(this.function);
    }

    protected ADD(ADD other) {
        this.dd = other.dd;
        this.function = other.function;
        this.variableStore = other.variableStore;
        BigcuddLibrary.Cudd_Ref(this.function);
    }

    /**
     * Overriding finalize in order to free CUDD allocated memory.
     */
    @Override
    protected void finalize() throws Throwable {
        BigcuddLibrary.Cudd_RecursiveDeref(dd, function);
        super.finalize();
    }

    public ADD plus(ADD other) {
        return apply(other, PLUS);
    }

    public ADD minus(ADD other) {
        return apply(other, MINUS);
    }

    public ADD times(ADD other) {
        return apply(other, TIMES);
    }

    public ADD dividedBy(ADD other) {
        return apply(other, DIVIDE);
    }

    public ADD and(ADD other) {
        return apply(other, TIMES);
    }

    public ADD or(ADD other) {
        return apply(other, LOGICAL_OR);
    }

    private ADD apply(ADD other, Cudd_addApply_arg1_callback operation) {
        Pointer<DdNode> result = BigcuddLibrary.Cudd_addApply(dd,
                                                              Pointer.getPointer(operation),
                                                              this.function,
                                                              other.function);
        return new ADD(dd, result, variableStore);
    }

    /**
     * @return negated form (corresponding to unary minus).
     */
    public ADD negate() {
        return new ADD(dd,
                       BigcuddLibrary.Cudd_addNegate(dd, this.function),
                       variableStore);
    }

    /**
     * @return complemented form (corresponding to logical not).
     */
    public ADD complement() {
        return new ADD(dd,
                       BigcuddLibrary.Cudd_addCmpl(dd, this.function),
                       variableStore);
    }

    /**
     * Implements if-then-else with the result of this boolean function
     * as the conditional.
     */
    public ADD ifThenElse(ADD ifTrue, ADD ifFalse) {
        Pointer<DdNode> result = BigcuddLibrary.Cudd_addIte(dd,
                                                            this.function,
                                                            ifTrue.function,
                                                            ifFalse.function);
        return new ADD(dd, result, variableStore);
    }

    /**
     * Overloading for constant fallbacks.
     */
    public ADD ifThenElse(ADD ifTrue, double ifFalse) {
        Pointer<DdNode> result = BigcuddLibrary.Cudd_addIte(dd,
                                                            this.function,
                                                            ifTrue.function,
                                                            BigcuddLibrary.Cudd_addConst(dd,
                                                                                         ifFalse));
        return new ADD(dd, result, variableStore);
    }

    public Set<String> getVariables() {
        Set<String> variables = new HashSet<String>();

        Pointer<Integer> variablesPtr = BigcuddLibrary.Cudd_SupportIndex(dd, this.function);
        int numVars = BigcuddLibrary.Cudd_ReadSize(dd);
        int[] variablesPresence = variablesPtr.getInts(numVars);
        for (short i = 0; i < numVars; i++) {
            if (variablesPresence[i] == 1) {
                variables.add(variableStore.getName(i));
            }
        }
        return variables;
    }

    public double eval(String[] variables) throws UnrecognizedVariableException {
        int[] presenceVector = variableStore.toPresenceVector(variables);
        Pointer<DdNode> terminal = BigcuddLibrary.Cudd_Eval(dd,
                                                            function,
                                                            Pointer.pointerToInts(presenceVector));
        DdNode terminalNode = terminal.get();
        return terminalNode.type().value();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        ADD other = (ADD) obj;
        return this.function.equals(other.function)
                || (BigcuddLibrary.Cudd_EqualSupNorm(dd,
                                                     this.function,
                                                     other.function,
                                                     ADD.FLOATING_POINT_PRECISION,
                                                     1) == 1);
    }

    Pointer<DdNode> getUnderlyingNode() {
        return this.function;
    }

    /**************************************************************
     *** Operators definitions
     *************************************************************/

    private static BigcuddLibrary.Cudd_addApply_arg1_callback TIMES = new BigcuddLibrary.Cudd_addApply_arg1_callback() {
        public Pointer<DdNode > apply(Pointer<BigcuddLibrary.DdManager > dd,
                                      Pointer<Pointer<DdNode > > node1,
                                      Pointer<Pointer<DdNode > > node2) {
            return BigcuddLibrary.Cudd_addTimes(dd, node1, node2);
        }
    };

    private static BigcuddLibrary.Cudd_addApply_arg1_callback PLUS = new BigcuddLibrary.Cudd_addApply_arg1_callback() {
        public Pointer<DdNode > apply(Pointer<BigcuddLibrary.DdManager > dd,
                                      Pointer<Pointer<DdNode > > node1,
                                      Pointer<Pointer<DdNode > > node2) {
            return BigcuddLibrary.Cudd_addPlus(dd, node1, node2);
        }
    };

    private static BigcuddLibrary.Cudd_addApply_arg1_callback DIVIDE = new BigcuddLibrary.Cudd_addApply_arg1_callback() {
        public Pointer<DdNode > apply(Pointer<BigcuddLibrary.DdManager > dd,
                                      Pointer<Pointer<DdNode > > node1,
                                      Pointer<Pointer<DdNode > > node2) {
            return BigcuddLibrary.Cudd_addDivide(dd, node1, node2);
        }
    };

    private static BigcuddLibrary.Cudd_addApply_arg1_callback MINUS = new BigcuddLibrary.Cudd_addApply_arg1_callback() {
        public Pointer<DdNode > apply(Pointer<BigcuddLibrary.DdManager > dd,
                                      Pointer<Pointer<DdNode > > node1,
                                      Pointer<Pointer<DdNode > > node2) {
            return BigcuddLibrary.Cudd_addMinus(dd, node1, node2);
        }
    };

    private static BigcuddLibrary.Cudd_addApply_arg1_callback LOGICAL_OR = new BigcuddLibrary.Cudd_addApply_arg1_callback() {
        public Pointer<DdNode > apply(Pointer<BigcuddLibrary.DdManager > dd,
                                      Pointer<Pointer<DdNode > > node1,
                                      Pointer<Pointer<DdNode > > node2) {
            return BigcuddLibrary.Cudd_addOr(dd, node1, node2);
        }
    };

}
