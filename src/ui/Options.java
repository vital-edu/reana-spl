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
        OptionSet options = optionParser.parse(args);

        Options result = new Options();
        result.featureModelFilePath = options.valueOf(featureModelOption);
        result.umlModelsFilePath = options.valueOf(umlModelsOption);

        return result;
    }
}
