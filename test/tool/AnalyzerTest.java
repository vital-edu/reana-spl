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

        analyzer.generateDotFile(reliability, "test/oxygenation.dot");

        ADD featureModel = solver.encodeFormula(fmBSN);
        ADD sqlite = jadd.getVariable("SQLite").times(jadd.makeConstant(0.9890587955100504));
        ADD memory = jadd.getVariable("Memory").times(jadd.makeConstant(0.9890587955100504));
        ADD file = jadd.getVariable("File").times(jadd.makeConstant(0.9890548353295385));
        ADD expected = featureModel.times(sqlite)
                       .plus(featureModel.times(memory))
                       .plus(featureModel.times(file));
        analyzer.generateDotFile(expected, "test/expected_oxygenation.dot");
        Assert.assertEquals(expected, reliability);
    }

}
