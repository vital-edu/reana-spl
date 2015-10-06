package tool;

import tool.RDGNode;
import fdtmc.FDTMC;

/**
 * RDG nodes for the BSN.
 * @author thiago
 *
 */
public class BSNNodes {

    public static RDGNode getSQLiteRDGNode() {
        FDTMC fdtmc = FDTMCStub.createSqliteFDTMC();
        RDGNode node = new RDGNode("sqlite", "SQLite", fdtmc);
        return node;
    }

    public static RDGNode getFileRDGNode() {
        FDTMC fdtmc = FDTMCStub.createFileFDTMC();
        RDGNode node = new RDGNode("file", "File", fdtmc);
        return node;
    }

    public static RDGNode getMemoryRDGNode() {
        FDTMC fdtmc = FDTMCStub.createMemoryFDTMC();
        RDGNode node = new RDGNode("memory", "Memory", fdtmc);
        return node;
    }

    public static RDGNode getOxygenationRDGNode() {
        FDTMC fdtmc = FDTMCStub.createOxygenationFDTMC();
        RDGNode node = new RDGNode("oxygenation", "Oxygenation", fdtmc);
        node.addDependency(getSQLiteRDGNode());
        node.addDependency(getFileRDGNode());
        node.addDependency(getMemoryRDGNode());
        return node;
    }

    public static RDGNode getPulseRateRDGNode() {
        FDTMC fdtmc = FDTMCStub.createPulseRateFDTMC();
        RDGNode node = new RDGNode("pulseRate", "PulseRate", fdtmc);
        node.addDependency(getSQLiteRDGNode());
        node.addDependency(getFileRDGNode());
        node.addDependency(getMemoryRDGNode());
        return node;
    }

    public static RDGNode getSituationRDGNode() {
        FDTMC fdtmc = FDTMCStub.createPulseRateFDTMC();
        RDGNode node = new RDGNode("situation", "true", fdtmc);
        node.addDependency(getOxygenationRDGNode());
        node.addDependency(getPulseRateRDGNode());
        return node;
    }

}
