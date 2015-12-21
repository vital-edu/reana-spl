package tool.analyzers.buildingblocks;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a component in an asset base.
 *
 * @param <T>
 */
public class Component<T> {

    private String id;
    private String presenceCondition;
    private T asset;
    private Collection<Component<T>> dependencies;

    public Component(String id, String presenceCondition, T asset, Collection<Component<T>> dependencies) {
        this.id = id;
        this.presenceCondition = presenceCondition;
        this.asset = asset;
        this.dependencies = dependencies;
    }

    public String getId() {
        return id;
    }

    public String getPresenceCondition() {
        return presenceCondition;
    }

    public T getAsset() {
        return asset;
    }

    public Collection<Component<T>> getDependencies() {
        return dependencies;
    }

    public <U> Component<U> map(Function<T, U> mapper) {
        Collection<Component<U>> mappedDependencies = this.getDependencies().stream()
                .map(c -> c.map(mapper))
                .collect(Collectors.toSet());
        return new Component<U>(this.getId(),
                                this.getPresenceCondition(),
                                mapper.apply(this.getAsset()),
                                mappedDependencies);
    }

}
