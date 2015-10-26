package tool;

import java.util.Arrays;

import jadd.ADD;
import jadd.JADD;
import jadd.UnrecognizedVariableException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import expressionsolver.ExpressionSolver;

public class AnalyzerTest {
    private static final String PARAM_PATH = "/opt/param-2-3-64";

    String fmBSN = "Root  &&  (!Root  ||  Monitoring)  &&  (!Root  ||  Storage)  &&  (!Monitoring  ||  Root)  &&  (!Storage  ||  Root)  &&  (!Monitoring  ||  SensorInformation)  &&  (!Monitoring  ||  Sensor)  &&  (!SensorInformation  ||  Monitoring)  &&  (!Sensor  ||  Monitoring)  &&  (!SensorInformation  ||  Oxygenation  ||  PulseRate  ||  Temperature  ||  Position  ||  Fall)  &&  (!Oxygenation  ||  SensorInformation)  &&  (!PulseRate  ||  SensorInformation)  &&  (!Temperature  ||  SensorInformation)  &&  (!Position  ||  SensorInformation)  &&  (!Fall  ||  SensorInformation)  &&  (!Sensor  ||  SPO2  ||  ECG  ||  TEMP  ||  ACC)  &&  (!SPO2  ||  Sensor)  &&  (!ECG  ||  Sensor)  &&  (!TEMP  ||  Sensor)  &&  (!ACC  ||  Sensor)  &&  (!Storage  ||  SQLite  ||  Memory  ||  File)  &&  (!SQLite  ||  Storage)  &&  (!Memory  ||  Storage)  &&  (!File  ||  Storage)  &&  (!SQLite  ||  !Memory)  &&  (!SQLite  ||  !File)  &&  (!Memory  ||  !File)  &&  (!Oxygenation  ||  SPO2)  &&  (!PulseRate  ||  SPO2  ||  ECG)  &&  (!Fall  ||  ACC)  &&  (!Position  ||  ACC)  &&  (!Temperature  ||  TEMP)  &&  True  &&  !False  &&  (PulseRate  ||  ACC  ||  Memory  ||  Temperature  ||  Position  ||  Storage  ||  SensorInformation  ||  Sensor  ||  TEMP  ||  Monitoring  ||  Oxygenation  ||  File  ||  SQLite  ||  ECG  ||  Fall  ||  SPO2  ||  True)";
    JADD jadd;
    ExpressionSolver solver;
    Analyzer analyzer;

    @Before
    public void setUp() throws Exception {
        jadd = new JADD();
        solver = new ExpressionSolver(jadd);
        analyzer = new Analyzer(jadd, fmBSN, PARAM_PATH);
    }

    @Test
    public void testEvaluateReliabilitySQLite() throws CyclicRdgException {
        RDGNode sqlite = BSNNodes.getSQLiteRDGNode();
        ADD reliability = analyzer.evaluateFeatureFamilyBasedReliability(sqlite);

        analyzer.generateDotFile(reliability, "test/sqlite.dot");

        ADD featureModel = solver.encodeFormula(fmBSN);
        ADD expected = featureModel.times(jadd.makeConstant(0.998001));
        Assert.assertEquals(expected, reliability);
    }

    @Test
    public void testEvaluateReliabilityOxygenation() throws UnrecognizedVariableException, CyclicRdgException {
        RDGNode node = BSNNodes.getOxygenationRDGNode();
        ADD reliability = analyzer.evaluateFeatureFamilyBasedReliability(node);

        String[] sqliteConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2",
                "SQLite"};
        Assert.assertEquals("Configuration with SQLite",
                0.9920279440699441, reliability.eval(sqliteConfig), 1E-14);

        String[] memoryConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2",
                "Memory"};
        Assert.assertEquals("Configuration with Memory",
                0.9920279440699441, reliability.eval(memoryConfig), 1E-14);

        String[] fileConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2",
                "File"};
        Assert.assertEquals("Configuration without SQLite or Memory",
                0.994014980014994001, reliability.eval(fileConfig), 1E-14);

        String[] noneConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2"};
        Assert.assertEquals("Invalid configuration",
                0, reliability.eval(noneConfig), 1E-14);
    }


    @Test
    public void testEvaluateFeatureProductReliabilityOxygenation() throws UnrecognizedVariableException, CyclicRdgException {
        RDGNode node = BSNNodes.getOxygenationRDGNode();

        String[] sqliteConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2",
                "SQLite"};
        double reliability = analyzer.evaluateFeatureProductBasedReliability(node, Arrays.asList(sqliteConfig));
        Assert.assertEquals("Configuration with SQLite",
                0.9920279440699441, reliability, 1E-14);

        String[] memoryConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2",
                "Memory"};
        reliability = analyzer.evaluateFeatureProductBasedReliability(node, Arrays.asList(memoryConfig));
        Assert.assertEquals("Configuration with Memory",
                0.9920279440699441, reliability, 1E-14);

        String[] fileConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2",
                "File"};
        reliability = analyzer.evaluateFeatureProductBasedReliability(node, Arrays.asList(fileConfig));
        Assert.assertEquals("Configuration without SQLite or Memory",
                0.994014980014994001, reliability, 1E-14);

        String[] noneConfig = new String[]{
                "Root",
                "Monitoring",
                "Storage",
                "SensorInformation",
                "Sensor",
                "Oxygenation",
                "SPO2"};
        reliability = analyzer.evaluateFeatureProductBasedReliability(node, Arrays.asList(noneConfig));
        Assert.assertEquals("Invalid configuration",
                0, reliability, 1E-14);
    }

}
