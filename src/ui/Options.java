package ui;

import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * Command-line options.
 * @author thiago
 *
 */
class Options {
    public String featureModelFilePath;
    public String umlModelsFilePath;
    public String configuration;
    public String configurationsFilePath;
    public boolean printAllConfigurations;

    static Options parseOptions(String[] args) throws IOException {
        OptionParser optionParser = new OptionParser();
        OptionSpec<String> featureModelOption = optionParser
                .accepts("feature-model")
                .withRequiredArg()
                .defaultsTo("fm.txt");
        OptionSpec<String> umlModelsOption = optionParser
                .accepts("uml-models")
                .withRequiredArg()
                .defaultsTo("modeling.xml");

        OptionSpec<String> configurationsFileOption = optionParser
                .accepts("configurations-file")
                .withRequiredArg()
                .defaultsTo("configurations.txt");
        OptionSpec<String> configurationOption = optionParser
                .accepts("configuration")
                .withRequiredArg();
        OptionSpec<Void> allConfigurationsOption = optionParser
                .accepts("all-configurations");

        OptionSpec<Void> helpOption = optionParser
                .accepts("help")
                .forHelp();

        OptionSet options = optionParser.parse(args);
        if (options.has(helpOption)) {
            optionParser.printHelpOn(System.out);
            System.exit(1);
        }

        Options result = new Options();
        result.featureModelFilePath = options.valueOf(featureModelOption);
        result.umlModelsFilePath = options.valueOf(umlModelsOption);
        result.configuration = options.valueOf(configurationOption);
        result.configurationsFilePath = options.valueOf(configurationsFileOption);
        result.printAllConfigurations = options.has(allConfigurationsOption);

        return result;
    }
}
