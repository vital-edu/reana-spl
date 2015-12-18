package tool.analyzers.buildingblocks;

@FunctionalInterface
public interface IfThenElse<P, U> {

    U apply(P presence, U ifPresent, U ifAbsent);

}
