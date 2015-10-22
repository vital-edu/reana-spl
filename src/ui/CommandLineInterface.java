/**
 *
 */
package ui;

import jadd.ADD;
import jadd.UnrecognizedVariableException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.DOMException;

import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.exceptions.InvalidTagException;
import parsing.exceptions.UnsupportedFragmentTypeException;
import tool.Analyzer;
import tool.PruningStrategyFactory;
import tool.RDGNode;
import tool.stats.IMemoryCollector;
import tool.stats.StatsCollectorFactory;

/**
 * Command-line application.
 *
 * @author thiago
 *
 */
public class CommandLineInterface {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Options options = Options.parseOptions(args);
        long startTime = System.currentTimeMillis();

        File featureModelFile = new File(options.getFeatureModelFilePath());
        File umlModels = new File(options.getUmlModelsFilePath());

        String featureModel = readFeatureModel(featureModelFile);
        StatsCollectorFactory statsCollectorFactory = new StatsCollectorFactory(options.hasStatsEnabled());
        IMemoryCollector memoryCollector = statsCollectorFactory.createMemoryCollector();

        Analyzer analyzer = new Analyzer(featureModel,
                                         statsCollectorFactory.createTimeCollector(),
                                         statsCollectorFactory.createFormulaCollector());
        analyzer.setPruningStrategy(PruningStrategyFactory.createPruningStrategy(options.getPruningStrategy()));

        RDGNode rdgRoot = null;
        memoryCollector.takeSnapshot("before model parsing");
        try {
            rdgRoot = analyzer.model(umlModels);
        } catch (DOMException | UnsupportedFragmentTypeException
                | InvalidTagException | InvalidNumberOfOperandsException
                | InvalidNodeClassException | InvalidNodeType e) {
            System.err.println("Error reading the provided UML Models.");
            e.printStackTrace();
            System.exit(1);
        }
        memoryCollector.takeSnapshot("after model parsing");

        memoryCollector.takeSnapshot("before evaluation");
        ADD familyReliability = analyzer.evaluateReliability(rdgRoot);
        memoryCollector.takeSnapshot("after evaluation");

        System.out.println("Configurations:");
        System.out.println("=========================================");
        if (options.hasPrintAllConfigurations()) {
            printAllConfigurationsValues(familyReliability);
        } else {
            printConfigurationsValues(options, familyReliability);
        }
        System.out.println("=========================================");

        analyzer.generateDotFile(familyReliability, "family-reliability.dot");
        System.out.println("Family-wide reliability decision diagram dumped at ./family-reliability.dot");

        if (options.hasStatsEnabled()) {
            analyzer.printStats();
            memoryCollector.printStats();
        }
        long totalRunningTime = System.currentTimeMillis() - startTime;
        System.out.println("Total running time: " +  totalRunningTime + " ms");
    }

    /**
     * @param options
     * @param familyReliability
     */
    private static void printConfigurationsValues(Options options, ADD familyReliability) {
        List<String> configurations = new LinkedList<String>();
        if (options.getConfiguration() != null) {
            configurations.add(options.getConfiguration());
        } else {
            Path configurationsFilePath = Paths.get(options.getConfigurationsFilePath());
            try {
                configurations.addAll(Files.readAllLines(configurationsFilePath, Charset.forName("UTF-8")));
            } catch (IOException e) {
                System.err.println("Error reading the provided configurations file.");
                e.printStackTrace();
            }
        }

        for (String configuration: configurations) {
            String[] variables = configuration.split(",");
            try {
                double reliability = familyReliability.eval(variables);
                printSingleConfiguration(configuration, reliability);
            } catch (UnrecognizedVariableException e) {
                System.err.println("Unrecognized variable: " + e.getVariableName());
            }
        }
    }

    private static void printAllConfigurationsValues(ADD familyReliability) {
        Map<List<String>, Double> configurations = familyReliability.getValidConfigurations();
        for (Map.Entry<List<String>, Double> entry: configurations.entrySet()) {
            printSingleConfiguration(entry.getKey().toString(),
                                     entry.getValue());
        }

        int count = 0;
        for (List<String> config: configurations.keySet()) {
            int tmpCount = 1;
            for (String feature: config) {
                if (feature.startsWith("(")) {
                    tmpCount *= 2;
                }
            }
            count += tmpCount;
        }
        System.out.println(">>>> Total configurations: " + count);
    }

    private static void printSingleConfiguration(String configuration, double reliability) {
        System.out.print(configuration + " --> ");
        if (Double.doubleToRawLongBits(reliability) != 0) {
            System.out.println(reliability);
        } else {
            System.out.println("INVALID");
        }
    }

    /**
     * @param featureModelFile
     * @return
     */
    private static String readFeatureModel(File featureModelFile) {
        String featureModel = null;
        Path path = featureModelFile.toPath();
        try {
            featureModel = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Error reading the provided Feature Model.");
            e.printStackTrace();
        }
        return featureModel;
    }

}
