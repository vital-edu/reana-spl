package tool;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RDGNodeTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testDependenciesTransitiveClosureContainsOwnNode() throws CyclicRdgException {
        RDGNode sqlite = BSNNodes.getSQLiteRDGNode();
        List<RDGNode> dependencies = sqlite.getDependenciesTransitiveClosure();

        Assert.assertEquals(1, dependencies.size());
        Assert.assertTrue(dependencies.contains(sqlite));
    }

    @Test
    public void testGetDependenciesTransitiveClosure() throws CyclicRdgException {
        RDGNode situation = BSNNodes.getSituationRDGNode();
        RDGNode pulseRate = BSNNodes.getPulseRateRDGNode();
        RDGNode sqlite = BSNNodes.getSQLiteRDGNode();
        RDGNode memory = BSNNodes.getMemoryRDGNode();
        RDGNode file = BSNNodes.getFileRDGNode();


        List<RDGNode> situationDependencies = situation.getDependenciesTransitiveClosure();
        Assert.assertTrue(situationDependencies.contains(situation));

        Assert.assertTrue(situationDependencies.indexOf(sqlite) < situationDependencies.indexOf(pulseRate));
        Assert.assertTrue(situationDependencies.indexOf(memory) < situationDependencies.indexOf(pulseRate));
        Assert.assertTrue(situationDependencies.indexOf(file) < situationDependencies.indexOf(pulseRate));

        Assert.assertTrue(situationDependencies.indexOf(sqlite) < situationDependencies.indexOf(situation));
        Assert.assertTrue(situationDependencies.indexOf(memory) < situationDependencies.indexOf(situation));
        Assert.assertTrue(situationDependencies.indexOf(file) < situationDependencies.indexOf(situation));

        Assert.assertTrue(situationDependencies.indexOf(pulseRate) < situationDependencies.indexOf(situation));
    }

    @Test
    public void testGetNumberOfPaths() throws CyclicRdgException {
        RDGNode situation = BSNNodes.getSituationRDGNode();
        RDGNode pulseRate = BSNNodes.getPulseRateRDGNode();
        RDGNode oxygenation = BSNNodes.getOxygenationRDGNode();
        RDGNode sqlite = BSNNodes.getSQLiteRDGNode();
        RDGNode memory = BSNNodes.getMemoryRDGNode();
        RDGNode file = BSNNodes.getFileRDGNode();

        Map<RDGNode, Integer> numberOfPaths = RDGNode.getNumberOfPaths();

        Assert.assertEquals(2, numberOfPaths.get(sqlite).intValue());
        Assert.assertEquals(2, numberOfPaths.get(memory).intValue());
        Assert.assertEquals(2, numberOfPaths.get(file).intValue());
        Assert.assertEquals(1, numberOfPaths.get(oxygenation).intValue());
        Assert.assertEquals(1, numberOfPaths.get(pulseRate).intValue());
        Assert.assertEquals(1, numberOfPaths.get(situation).intValue());
    }

}
