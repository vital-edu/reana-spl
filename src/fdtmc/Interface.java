package fdtmc;

/**
 * Represents an abstracted away FDTMC fragment.
 *
 * @author thiago
 */
public class Interface {
    private String abstractedId;
    private State initial;
    private State success;
    private State error;
    private Transition successTransition;
    private Transition errorTransition;

    public Interface(String abstractedId, State initial, State success, State error, Transition successTransition, Transition errorTransition) {
        this.abstractedId = abstractedId;
        this.initial = initial;
        this.success = success;
        this.error = error;
        this.successTransition = successTransition;
        this.errorTransition = errorTransition;
    }

    public State getInitial() {
        return initial;
    }

    public State getSuccess() {
        return success;
    }

    public State getError() {
        return error;
    }

    public Transition getSuccessTransition() {
        return successTransition;
    }

    public Transition getErrorTransition() {
        return errorTransition;
    }

    public String getAbstractedId() {
        return abstractedId;
    }

}
