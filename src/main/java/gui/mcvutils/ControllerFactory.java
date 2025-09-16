package gui.mcvutils;

import gui.Controller;
import init.Init;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import logging.LoggerManager;

/**
 * Singleton and factory design, there's no need to have more than one instance of every controller.
 * Uses reflection to have a cleaner code, even if it isn't the most efficient way
 * @author Fedrix
 */

public final class ControllerFactory {
    /* View reference must be set right after controller creation */
    private static final List<Controller> controllers = new LinkedList<>();
    
    public static <T extends Controller> T getController(Class<T> controllerClass) {
        for(Controller current : controllers){
            if(controllerClass.isInstance(current))
                return (controllerClass.cast(current));
        }
        T newController = null;
        try {
            newController = controllerClass.getDeclaredConstructor().newInstance();
            controllers.add(newController);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            LoggerManager.exception().log(Level.SEVERE, "Cannot create a new controller", ex);
            Init.shutdown(-1);
        }
        return newController;
    }
    
}
