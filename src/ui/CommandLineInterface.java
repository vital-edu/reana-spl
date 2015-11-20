/**
 *
 */
package ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import modeling.DiagramAPI;

import org.w3c.dom.DOMException;

import parsing.exceptions.InvalidNodeClassException;
import parsing.exceptions.InvalidNodeType;
import parsing.exceptions.InvalidNumberOfOperandsException;
import parsing.exceptions.InvalidTagException;
import parsing.exceptions.UnsupportedFragmentTypeException;
import tool.Analyzer;
import tool.CyclicRdgException;
import tool.PruningStrategyFactory;
import tool.RDGNode;
import tool.UnknownFeatureException;
import tool.analyzers.IReliabilityAnalysisResults;
import tool.stats.CollectibleTimers;
import tool.stats.IFormulaCollector;
import tool.stats.IMemoryCollector;
import tool.stats.ITimeCollector;
import ui.stats.StatsCollectorFactory;

/**
 * Command-line application.
 *
 * @author thiago
 *
 */
public class CommandLineInterface {
    private static final Logger LOGGER = Logger.getLogger(CommandLineInterface.class.getName());
    private static final PrintStream OUTPUT = System.out;

    private static IMemoryCollector memoryCollector;
    private static ITimeCollector timeCollector;
    private static IFormulaCollector formulaCollector;

    private CommandLineInterface() {
        // NO-OP
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        Options options = Options.parseOptions(args);
        LogManager logManager = LogManager.getLogManager();
        logManager.readConfiguration(new FileInputStream("logging.properties"));
        initializeStatsCollectors(options);

        memoryCollector.takeSnapshot("before model parsing");
        RDGNode rdgRoot = buildRDG(options);
        memoryCollector.takeSnapshot("after model parsing");

        Analyzer analyzer = makeAnalyzer(options);
        Collection<List<String>> targetConfigurations = getTargetConfigurations(options, analyzer);

        memoryCollector.takeSnapshot("before evaluation");
        IReliabilityAnalysisResults familyReliability = evaluateReliability(analyzer, rdgRoot, targetConfigurations, options);
        memoryCollector.takeSnapshot("after evaluation");

        printAnalysisResults(targetConfigurations, familyReliability);

        if (options.hasStatsEnabled()) {
            printStats(OUTPUT, familyReliability);
        }
        long totalRunningTime = System.currentTimeMillis() - startTime;
        OUTPUT.println("Total running time: " +  totalRunningTime + " ms");
    }

    /**
     * @param analyzer
     * @param rdgRoot
     * @param options
     * @return
     */
    private static IReliabilityAnalysisResults evaluateReliability(Analyzer analyzer, RDGNode rdgRoot, Collection<List<String>> configurations, Options options) {
        IReliabilityAnalysisResults results = null;
        switch (options.getAnalysisStrategy()) {
        case FEATURE_PRODUCT:
            results = evaluateReliability(analyzer::evaluateFeatureProductBasedReliability,
                                          rdgRoot,
                                          configurations);
            break;
        case PRODUCT:
            results = evaluateReliability(analyzer::evaluateProductBasedReliability,
                                          rdgRoot,
                                          configurations);
            break;
        case FAMILY:
            results = evaluateReliability(analyzer::evaluateFamilyBasedReliability,
                                          rdgRoot,
                                          configurations);
            break;
        case FAMILY_PRODUCT:
            results = evaluateReliability(analyzer::evaluateFamilyProductBasedReliability,
                                          rdgRoot,
                                          configurations);
            break;
        case FEATURE_FAMILY:
        default:
            results = evaluateFeatureFamilyBasedReliability(analyzer,
                                                            rdgRoot,
                                                            options);
        }
        return results;
    }

    private static IReliabilityAnalysisResults evaluateFeatureFamilyBasedReliability(Analyzer analyzer, RDGNode rdgRoot, Options options) {
        IReliabilityAnalysisResults results = null;
        String dotOutput = "family-reliability.dot";
        try {
            analyzer.setPruningStrategy(PruningStrategyFactory.createPruningStrategy(options.getPruningStrategy()));
            results = analyzer.evaluateFeatureFamilyBasedReliability(rdgRoot, dotOutput);
        } catch (CyclicRdgException e) {
            LOGGER.severe("Cyclic dependency detected in RDG.");
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(2);
        }
        OUTPUT.println("Family-wide reliability decision diagram dumped at " + dotOutput);
        return results;
    }

    private static IReliabilityAnalysisResults evaluateReliability(BiFunction<RDGNode, Collection<List<String>>, IReliabilityAnalysisResults> analyzer,
                                                                   RDGNode rdgRoot,
                                                                   Collection<List<String>> configurations) {
        IReliabilityAnalysisResults results = null;
        try {
            results = analyzer.apply(rdgRoot, configurations);
        } catch (CyclicRdgException e) {
            LOGGER.severe("Cyclic dependency detected in RDG.");
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(2);
        } catch (UnknownFeatureException e) {
            LOGGER.severe("Unrecognized feature: " + e.getFeatureName());
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return results;
    }

    /**
     * @param options
     * @return
     */
    private static Analyzer makeAnalyzer(Options options) {
        File featureModelFile = new File(options.getFeatureModelFilePath());
        String featureModel = readFeatureModel(featureModelFile);

        String paramPath = options.getParamPath();
        Analyzer analyzer = new Analyzer(featureModel,
                                         paramPath,
                                         timeCollector,
                                         formulaCollector);
        return analyzer;
    }

    /**
     * @param options
     */
    private static void initializeStatsCollectors(Options options) {
        StatsCollectorFactory statsCollectorFactory = new StatsCollectorFactory(options.hasStatsEnabled());
        memoryCollector = statsCollectorFactory.createMemoryCollector();
        timeCollector = statsCollectorFactory.createTimeCollector();
        formulaCollector = statsCollectorFactory.createFormulaCollector();
    }

    private static Collection<List<String>> getTargetConfigurations(Options options, Analyzer analyzer) {
        if (options.hasPrintAllConfigurations()) {
            return analyzer.getValidConfigurations();
        } else {
            Set<List<String>> configurations = new HashSet<List<String>>();

            List<String> rawConfigurations = new LinkedList<String>();
            if (options.getConfiguration() != null) {
                rawConfigurations.add(options.getConfiguration());
            } else {
                Path configurationsFilePath = Paths.get(options.getConfigurationsFilePath());
                try {
                    rawConfigurations.addAll(Files.readAllLines(configurationsFilePath, Charset.forName("UTF-8")));
                } catch (IOException e) {
                    LOGGER.severe("Error reading the provided configurations file.");
                    LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }

            for (String rawConfiguration: rawConfigurations) {
                String[] variables = rawConfiguration.split(",");
                configurations.add(Arrays.asList(variables));
            }

            return configurations;
        }
    }

    private static void printAnalysisResults(Collection<List<String>> configurations, IReliabilityAnalysisResults familyReliability) {
        OUTPUT.println("Configurations:");
        OUTPUT.println("=========================================");

        for (List<String> configuration: configurations) {
            try {
                String[] configurationAsArray = configuration.toArray(new String[configuration.size()]);
                printSingleConfiguration(configuration.toString(),
                                         familyReliability.getResult(configurationAsArray));
            } catch (UnknownFeatureException e) {
                LOGGER.severe("Unrecognized feature: " + e.getFeatureName());
                LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }

        int count = 0;
        for (List<String> config: configurations) {
            int tmpCount = 1;
            for (String feature: config) {
                if (feature.startsWith("(")) {
                    tmpCount *= 2;
                }
            }
            count += tmpCount;
        }

        OUTPUT.println("=========================================");
        OUTPUT.println(">>>> Total configurations: " + count);
    }

    private static void printSingleConfiguration(String configuration, double reliability) {
        String message = configuration + " --> ";
        if (Double.doubleToRawLongBits(reliability) != 0) {
            OUTPUT.println(message + reliability);
        } else {
            OUTPUT.println(message + "INVALID");
        }
    }

    private static void printStats(PrintStream out, IReliabilityAnalysisResults familyReliability) {
        out.println("-----------------------------");
        out.println("Stats:");
        out.println("------");
        timeCollector.printStats(out);
        formulaCollector.printStats(out);
        memoryCollector.printStats(out);
        printEvaluationReuse();
        familyReliability.printStats(out);
    }

    private static void printEvaluationReuse() {
        try {
            Map<RDGNode, Integer> numberOfPaths = RDGNode.getNumberOfPaths();
            int nodes = 0;
            int totalPaths = 0;
            for (Map.Entry<RDGNode, Integer> entry: numberOfPaths.entrySet()) {
                nodes++;
                totalPaths += entry.getValue();
                OUTPUT.println(entry.getKey().getId() + ": " + entry.getValue() + " paths");
            }
            OUTPUT.println("Evaluation economy because of cache: " + 100*(totalPaths-nodes)/(float)totalPaths + "%");
        } catch (CyclicRdgException e) {
            LOGGER.severe("Cyclic dependency detected in RDG.");
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(2);
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
            LOGGER.severe("Error reading the provided Feature Model.");
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(1);
        }
        return featureModel;
    }

    /**
     * @param options
     * @return
     */
    private static RDGNode buildRDG(Options options) {
        File umlModels = new File(options.getUmlModelsFilePath());
        RDGNode rdgRoot = null;
        try {
            rdgRoot = model(umlModels, timeCollector);
        } catch (DOMException | UnsupportedFragmentTypeException
                | InvalidTagException | InvalidNumberOfOperandsException
                | InvalidNodeClassException | InvalidNodeType e) {
            LOGGER.severe("Error reading the provided UML Models.");
            LOGGER.log(Level.SEVERE, e.toString(), e);
            System.exit(1);
        }
        return rdgRoot;
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
    private static RDGNode model(File umlModels, ITimeCollector timeCollector) throws UnsupportedFragmentTypeException, InvalidTagException, InvalidNumberOfOperandsException, InvalidNodeClassException, InvalidNodeType {
        timeCollector.startTimer(CollectibleTimers.PARSING_TIME);

        DiagramAPI modeler = new DiagramAPI(umlModels);
        RDGNode result = modeler.transform();

        timeCollector.stopTimer(CollectibleTimers.PARSING_TIME);
        return result;
    }

}
