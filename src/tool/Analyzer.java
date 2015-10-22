/**
 *
 */
package tool;

import jadd.ADD;
import jadd.JADD;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import modeling.DiagramAPI;

import org.w3c.dom.DOMException;

import paramwrapper.ParamWrapper;
import paramwrapper.ParametricModelChecker;
import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.exceptions.InvalidTagException;
import parsing.exceptions.UnsupportedFragmentTypeException;
import tool.stats.IFormulaCollector;
import tool.stats.ITimeCollector;
import expressionsolver.ExpressionSolver;
import fdtmc.FDTMC;

/**
 * Implements the orchestration of analysis tasks.
 *
 * This is the Façade to the domain model.
 * Its responsibility is establishing **what** needs to be done.
 *
 * @author thiago
 */
public class Analyzer {

    private ADD featureModel;
    private ParametricModelChecker modelChecker;
    private ExpressionSolver expressionSolver;
    private Map<String, ADD> reliabilityCache;
    private JADD jadd;
    private IPruningStrategy pruningStrategy;

    private ITimeCollector timeCollector;
    private IFormulaCollector formulaCollector;

    /**
     * Creates an Analyzer which will follow the logical rules
     * encoded in the provided feature model file.
     *
     * @param featureModel String containing a CNF view of the Feature Model
     *          expressed using Java logical operators.
     * @throws IOException if there is a problem reading the file.
     */
    public Analyzer(String featureModel, ITimeCollector timeCollector, IFormulaCollector formulaCollector) {
        this(new JADD(), featureModel);

        this.timeCollector = timeCollector;
        this.formulaCollector = formulaCollector;
    }

    /**
     * Package-private constructor for testability.
     * It allows injection of ADD processor an feature model expression.
     * @param jadd
     * @param featureModel
     */
    Analyzer(JADD jadd, String featureModel) {
        this.modelChecker = new ParamWrapper();
        this.jadd = jadd;
        this.expressionSolver = new ExpressionSolver(jadd);
        this.featureModel = expressionSolver.encodeFormula(featureModel);
        this.reliabilityCache = new HashMap<String, ADD>();
    }

    /**
     * Sets the pruning strategy to be used for preventing calculation
     * of reliability values for invalid configurations.
     *
     * If none is set, the default behavior is to multiply the reliability
     * mappings by the feature model's 0,1-ADD (so that valid configurations
     * yield the same reliability, but invalid ones yield 0).
     *
     * @param pruningStrategy the pruningStrategy to set
     */
    public void setPruningStrategy(IPruningStrategy pruningStrategy) {
        this.pruningStrategy = pruningStrategy;
    }

    /**
     * Abstracts UML to RDG transformation.
     *
     * @param umlModels
     * @return
     * @throws InvalidTagException
     * @throws UnsupportedFragmentTypeException
     * @throws DOMException
     * @throws InvalidNodeType
     * @throws InvalidNodeClassException
     * @throws InvalidNumberOfOperandsException
     */
    public RDGNode model(File umlModels) throws UnsupportedFragmentTypeException, InvalidTagException, InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
        timeCollector.startParsingTimer();

        DiagramAPI modeler = new DiagramAPI(umlModels);
        RDGNode result = modeler.transform();

        timeCollector.stopParsingTimer();
        return result;
    }

    /**
     * Evaluates the family-based reliability function of an RDG node, based
     * on the reliabilities of the nodes on which it depends.
     *
     * A reliability function is a boolean function from the set of features
     * to Real values, where the reliability of any invalid configuration is 0.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     */
    public ADD evaluateReliability(RDGNode node) {
        ADD reliability = evaluatePartialReliability(node);

        timeCollector.startFamilyBasedTimer();
        // After evaluating the expression, constant terms alter the {0,1} nature
        // of the reliability ADD. Thus, we must multiply the result by the
        // {0,1} representation of the feature model in order to retain 0 as the
        // value for invalid configurations.
        ADD result = featureModel.times(reliability);
        timeCollector.stopFamilyBasedTimer();
        return result;
    }

    /**
     * Dumps the computed family reliability function to the output file
     * in the specified path.
     *
     * @param familyReliability Reliability function computed by a call to the
     *          {@link #evaluateReliability(RDGNode)} method.
     * @param outputFile Path to the .dot file to be generated.
     */
    public void generateDotFile(ADD familyReliability, String outputFile) {
        jadd.dumpDot("Family Reliability", familyReliability, outputFile);
    }

    /**
     * Computes the reliability expression for the model of a given RDG node.
     *
     * @param node
     * @return an algebraic expression on the variables present in the node's model.
     */
    private String getReliabilityExpression(RDGNode node) {
        FDTMC model = node.getFDTMC();
        return modelChecker.getReliability(model);
    }

    /**
     * Recursively evaluates the reliability function of an RDG node.
     * A reliability function is a boolean function from the set of features
     * to Real values.
     *
     * @param node RDG node whose reliability is to be evaluated.
     * @return
     */
    private ADD evaluatePartialReliability(RDGNode node) {
        if (isInCache(node)) {
            return getCachedReliability(node);
        }

        timeCollector.startFeatureBasedTimer();
        String reliabilityExpression = getReliabilityExpression(node);
        timeCollector.stopFeatureBasedTimer();
        formulaCollector.collectFormula(reliabilityExpression);

        System.out.println("Reliability expression for "+ node.getId() + " -> " + reliabilityExpression);

        Map<String, ADD> childrenReliabilities = new HashMap<String, ADD>();
        for (RDGNode child: node.getDependencies()) {
            ADD childReliability = evaluatePartialReliability(child);

            timeCollector.startFamilyBasedTimer();
            ADD presenceCondition = expressionSolver.encodeFormula(child.getPresenceCondition());

            ADD phi = presenceCondition.ifThenElse(childReliability, 1);
            childrenReliabilities.put(child.getId(),
                                      phi);
            timeCollector.stopFamilyBasedTimer();
        }

        timeCollector.startFamilyBasedTimer();
        ADD reliability = expressionSolver.solveExpression(reliabilityExpression,
                                                           childrenReliabilities);
        reliability = pruningStrategy.pruneInvalidConfigurations(node,
                                                                 reliability,
                                                                 featureModel);
        timeCollector.stopFamilyBasedTimer();

        cacheReliability(node, reliability);
        jadd.dumpDot(reliabilityExpression,
                     reliability,
                     "result-"+node.getId()+".dot");
        return reliability;
    }

    private ADD getCachedReliability(RDGNode node) {
        return reliabilityCache.get(node.getId());
    }

    private boolean isInCache(RDGNode node) {
        return reliabilityCache.containsKey(node.getId());
    }

    private void cacheReliability(RDGNode node, ADD reliability) {
        reliabilityCache.put(node.getId(), reliability);
    }

    public void printStats() {
        System.out.println("-----------------------------");
        System.out.println("Stats:");
        System.out.println("------");
        timeCollector.printStats();
        formulaCollector.printStats();
    }

}
