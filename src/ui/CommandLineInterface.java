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

import tool.Analyzer;
import tool.RDGNode;
import Parsing.Exceptions.InvalidNodeClassException;
import Parsing.Exceptions.InvalidNodeType;
import Parsing.Exceptions.InvalidNumberOfOperandsException;
import Parsing.Exceptions.InvalidTagException;
import Parsing.Exceptions.UnsupportedFragmentTypeException;

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

        File featureModelFile = new File(options.featureModelFilePath);
        File umlModels = new File(options.umlModelsFilePath);

        String featureModel = readFeatureModel(featureModelFile);
        Analyzer analyzer = new Analyzer(featureModel);
        RDGNode rdgRoot = null;
        try {
            rdgRoot = analyzer.model(umlModels);
        } catch (DOMException | UnsupportedFragmentTypeException
                | InvalidTagException | InvalidNumberOfOperandsException
                | InvalidNodeClassException | InvalidNodeType e) {
            System.err.println("Error reading the provided UML Models.");
            e.printStackTrace();
        }

        ADD familyReliability = analyzer.evaluateReliability(rdgRoot);

        System.out.println("Configurations:");
        System.out.println("=========================================");
        if (options.printAllConfigurations) {
            printAllConfigurationsValues(familyReliability);
        } else {
            printConfigurationsValues(options, familyReliability);
        }
        System.out.println("=========================================");

        analyzer.generateDotFile(familyReliability, "family-reliability.dot");
        System.out.println("Family-wide reliability decision diagram dumped at ./family-reliability.dot");
    }

    /**
     * @param options
     * @param familyReliability
     */
    private static void printConfigurationsValues(Options options, ADD familyReliability) {
        List<String> configurations = new LinkedList<String>();
        if (options.configuration != null) {
            configurations.add(options.configuration);
        } else {
            Path configurationsFilePath = Paths.get(options.configurationsFilePath);
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
    }

    private static void printSingleConfiguration(String configuration, double reliability) {
        System.out.print(configuration + " --> ");
        if (reliability != 0) {
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
