package tool.analyzers.buildingblocks;

public class IfOperator<U> implements IfThenElse<Boolean, U> {

    @Override
    public U apply(Boolean presence, U ifPresent, U ifAbsent) {
        if (presence) {
            return ifPresent;
        } else {
            return ifAbsent;
        }
    }

}
