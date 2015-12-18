package tool.analyzers.buildingblocks;

import java.util.Map;

public interface AssetProcessor<T, U> {

    U apply(T asset, Map<String, U> values);

}
