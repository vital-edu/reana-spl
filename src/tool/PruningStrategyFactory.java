package tool;

public class PruningStrategyFactory {

    private PruningStrategyFactory() {
        // NO-OP
    }

    public static IPruningStrategy createPruningStrategy(PruningStrategy strategySelection) {
        switch (strategySelection) {
        case NONE:
            return new NoPruningStrategy();
        case FM:
        default:
            return new FeatureModelPruningStrategy();
        }
    }

}
