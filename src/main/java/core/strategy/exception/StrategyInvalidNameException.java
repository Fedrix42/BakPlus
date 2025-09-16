package core.strategy.exception;

import core.strategy.Strategy;

/**
 * A name of a strategy is valid if the check against it's regex match
 * @author Fedrix
 */
public class StrategyInvalidNameException extends Exception {

    public StrategyInvalidNameException() {
        super("The strategy name is not valid. It must match the following format: " + Strategy.NAME_REGEX);
    }
    
}
