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
        // TODO Auto-generated method stub
        // TODO Use a CLI parser library to get options
        File featureModelFile = new File("fm.txt");
        File umlModels = new File("modeling.xml");

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
