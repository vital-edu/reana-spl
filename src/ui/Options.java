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
    private String featureModelFilePath;
    private String umlModelsFilePath;
    private String configuration;
    private String configurationsFilePath;
    private boolean printAllConfigurations;
    private boolean statsEnabled;

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
        OptionSpec<Void> statsEnabledOption = optionParser
                .accepts("stats");

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
        result.statsEnabled = options.has(statsEnabledOption);

        return result;
    }

    public String getFeatureModelFilePath() {
        return featureModelFilePath;
    }

    public String getUmlModelsFilePath() {
        return umlModelsFilePath;
    }

    public boolean hasStatsEnabled() {
        return statsEnabled;
    }

    public boolean hasPrintAllConfigurations() {
        return printAllConfigurations;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getConfigurationsFilePath() {
        return configurationsFilePath;
    }

}
