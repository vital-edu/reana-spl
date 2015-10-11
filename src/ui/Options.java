package ui;

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

    static Options parseOptions(String[] args) {
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

        OptionSet options = optionParser.parse(args);

        Options result = new Options();
        result.featureModelFilePath = options.valueOf(featureModelOption);
        result.umlModelsFilePath = options.valueOf(umlModelsOption);
        result.configuration = options.valueOf(configurationOption);
        result.configurationsFilePath = options.valueOf(configurationsFileOption);

        return result;
    }
}
