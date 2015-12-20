package tool.analyzers.functional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import paramwrapper.ParametricModelChecker;
import tool.Analyzer;
import tool.RDGNode;
import tool.analyzers.buildingblocks.DerivationFunction;
import fdtmc.FDTMC;

public class FamilyBasedFirstPhase {
    private static final Logger LOGGER = Logger.getLogger(FamilyBasedFirstPhase.class.getName());

    ParametricModelChecker modelChecker;

    /**
     * LAMBDA_v
     */
    private DerivationFunction<String, FDTMC, FDTMC> derive150Model;


    public FamilyBasedFirstPhase(ParametricModelChecker modelChecker) {
        this.modelChecker = modelChecker;

        derive150Model = DerivationFunction.abstractDerivation(FDTMC::ifThenElse,
                                                               FDTMC::inline,
                                                               new FDTMC());
    }

    /**
     * Computes the reliability expression for the 150% model of the given RDG node,
     * using the given order of nodes for sequential composition.
     *
     * The returned expression has variables encoding the presence/absence of
     * RDG nodes (components).
     *
     * This function implements the family-based first phase of analyses.
     *
     * @see {@link Analyzer.getReliabilityExpression}
     * @param node
     * @return
     */
    public String getReliabilityExpression(List<RDGNode> dependencies) {
        FDTMC derived150Model = deriveFromMany(dependencies);
        String expression = modelChecker.getReliability(derived150Model);
        LOGGER.info("Parametric model-checking ok...");
        return expression;
    }

    // TODO Candidate!
    private FDTMC deriveFromMany(List<RDGNode> dependencies) {
        Map<String, FDTMC> derivedModels = new HashMap<String, FDTMC>();
        return dependencies.stream()
                .map(n -> deriveNode(n, derive150Model, derivedModels))
                .reduce((first, fdtmc) -> fdtmc)
                .get();
    }

    // TODO Candidate!
    private FDTMC deriveNode(RDGNode node, DerivationFunction<String, FDTMC, FDTMC> derive, Map<String, FDTMC> derivedModels) {
        String presence = node.getId();
        FDTMC derived = derive.apply(presence, node.getFDTMC(), derivedModels);
        derivedModels.put(node.getId(), derived);
        return derived;
    }

}
