package tool.analyzers.buildingblocks;

@FunctionalInterface
public interface IsPresent<T, P> {

    P apply(Component<T> component);

}
