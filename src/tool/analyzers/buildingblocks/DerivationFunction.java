package tool.analyzers.buildingblocks;

import java.util.Map;

@FunctionalInterface
public interface DerivationFunction<P, A, V> {

    V apply(P presence, A asset, Map<String, V> values);

    static <P, T, D> DerivationFunction<P, T, D> abstractDerivation(IfThenElse<P, D> ite,
                                                                    AssetProcessor<T, D> processAsset,
                                                                    D defaultValue) {
        return (presence, asset, values) -> ite.apply(presence,
                                                      processAsset.apply(asset, values),
                                                      defaultValue);
    }
}
