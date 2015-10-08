package tool;

import jadd.ADD;
import jadd.JADD;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import expressionsolver.ExpressionSolver;

public class AnalyzerTest {

    String fmBSN = "Root  &&  (!Root  ||  Monitoring)  &&  (!Root  ||  Storage)  &&  (!Monitoring  ||  Root)  &&  (!Storage  ||  Root)  &&  (!Monitoring  ||  SensorInformation)  &&  (!Monitoring  ||  Sensor)  &&  (!SensorInformation  ||  Monitoring)  &&  (!Sensor  ||  Monitoring)  &&  (!SensorInformation  ||  Oxygenation  ||  PulseRate  ||  Temperature  ||  Position  ||  Fall)  &&  (!Oxygenation  ||  SensorInformation)  &&  (!PulseRate  ||  SensorInformation)  &&  (!Temperature  ||  SensorInformation)  &&  (!Position  ||  SensorInformation)  &&  (!Fall  ||  SensorInformation)  &&  (!Sensor  ||  SPO2  ||  ECG  ||  TEMP  ||  ACC)  &&  (!SPO2  ||  Sensor)  &&  (!ECG  ||  Sensor)  &&  (!TEMP  ||  Sensor)  &&  (!ACC  ||  Sensor)  &&  (!Storage  ||  SQLite  ||  Memory  ||  File)  &&  (!SQLite  ||  Storage)  &&  (!Memory  ||  Storage)  &&  (!File  ||  Storage)  &&  (!SQLite  ||  !Memory)  &&  (!SQLite  ||  !File)  &&  (!Memory  ||  !File)  &&  (!Oxygenation  ||  SPO2)  &&  (!PulseRate  ||  SPO2  ||  ECG)  &&  (!Fall  ||  ACC)  &&  (!Position  ||  ACC)  &&  (!Temperature  ||  TEMP)  &&  True  &&  !False  &&  (PulseRate  ||  ACC  ||  Memory  ||  Temperature  ||  Position  ||  Storage  ||  SensorInformation  ||  Sensor  ||  TEMP  ||  Monitoring  ||  Oxygenation  ||  File  ||  SQLite  ||  ECG  ||  Fall  ||  SPO2  ||  True)";
    JADD jadd;
    ExpressionSolver solver;
    Analyzer analyzer;

    @Before
    public void setUp() throws Exception {
        jadd = new JADD();
        solver = new ExpressionSolver(jadd);
        analyzer = new Analyzer(jadd, fmBSN);
    }

    @Test
    public void testEvaluateReliabilitySQLite() {
        RDGNode sqlite = BSNNodes.getSQLiteRDGNode();
        ADD reliability = analyzer.evaluateReliability(sqlite);

        analyzer.generateDotFile(reliability, "test/sqlite.dot");

        ADD featureModel = solver.encodeFormula(fmBSN);
        ADD expected = featureModel.times(jadd.makeConstant(0.998001));
        Assert.assertEquals(expected, reliability);
    }

    @Test
    public void testEvaluateReliabilityOxygenation() {
        RDGNode node = BSNNodes.getOxygenationRDGNode();
        ADD reliability = analyzer.evaluateReliability(node);

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

}
