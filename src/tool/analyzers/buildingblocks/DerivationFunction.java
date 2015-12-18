package tool.analyzers.buildingblocks;

import java.util.Map;

@FunctionalInterface
public interface DerivationFunction<P, T, U> {

    U apply(P presence, T asset, Map<String, U> values);

    static <P, T, U> DerivationFunction<P, T, U> abstractDerivation(IfThenElse<P, U> ite,
                                                                    AssetProcessor<T, U> processAsset,
                                                                    U defaultValue) {
        return (presence, asset, values) -> ite.apply(presence,
                                                      processAsset.apply(asset, values),
                                                      defaultValue);
    }
}
