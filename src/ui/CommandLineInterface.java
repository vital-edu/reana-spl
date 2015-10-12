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
     */
    public static void main(String[] args) {
        Options options = Options.parseOptions(args);

        File featureModelFile = new File(options.featureModelFilePath);
        File umlModels = new File(options.umlModelsFilePath);

        String featureModel = null;
        Path path = featureModelFile.toPath();
        try {
            featureModel = new String(Files.readAllBytes(path), Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.err.println("Error reading the provided Feature Model.");
            e.printStackTrace();
        }
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

        System.out.println("=========================================");
        System.out.println("Configurations:");
        for (String configuration: configurations) {
            String[] variables = configuration.split(",");
            System.out.print(configuration + " --> ");
            try {
                double reliability = familyReliability.eval(variables);
                if (reliability != 0) {
                    System.out.println(reliability);
                } else {
                    System.out.println("INVALID");
                }
            } catch (UnrecognizedVariableException e) {
                System.out.println("Unrecognized variable: " + e.getVariableName());
            }
        }

        analyzer.generateDotFile(familyReliability, "family-reliability.dot");
        System.out.println("Family-wide reliability decision diagram dumped at ./family-reliability.dot");
    }

}
