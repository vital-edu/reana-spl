package tool.analyzers.functional;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductIterationHelper {

    public enum CONCURRENCY {
        PARALLEL,
        SEQUENTIAL
    };

    public static <T> Map<Collection<String>, Double> evaluate(Function<Collection<String>, Double> eval,
                                                               Collection<Collection<String>> configurations,
                                                               CONCURRENCY mode) {
        Stream<Collection<String>> configs = configurations.stream();
        if (mode == CONCURRENCY.PARALLEL) {
            configs = configs.parallel();
        }

        Map<Collection<String>, Double> results = configs
                .collect(Collectors.toMap(Function.identity(),
                                          eval));
        return results;
    }

}
