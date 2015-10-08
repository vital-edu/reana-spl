/**
 *
 */
package ui;

import jadd.ADD;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import tool.Analyzer;
import tool.RDGNode;

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
        RDGNode rdgRoot = analyzer.model(umlModels);
        ADD familyReliability = analyzer.evaluateReliability(rdgRoot);
        analyzer.generateDotFile(familyReliability, "family-reliability.dot");
    }

}
