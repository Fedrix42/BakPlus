package gui.mcvutils;

import gui.Controller;
import gui.Controller;
import gui.Model;
import gui.Model;
import gui.View;


/**
 * Factory to create frames, dialogs and view in general
 * This should be used to create new views object because it
 * assure that the references between controller and view are correct
 * @author Fedrix
 */
public final class InitMCV {
    /**
     * Set all references between controller, view and model
     * This will also update the model calling
     * @see updateViewAndModel() of Model interface
     * Every passed argument can be null, in that case references for that null object are not set
     * @param <viewT> view interface type
     * @param <ctrlT> controller interface type
     * @param <modelT> model interface type
     * @param controller
     * @param view
     * @param model
     * @return the view passed as argument (which can be null)
     */
    public static <viewT extends View, ctrlT extends Controller, modelT extends Model> viewT 
            setReferencesAndUpdate(ctrlT controller, viewT view, modelT model) {
        if(controller != null){
            controller.setView(view);
            controller.setModel(model);
        }
        if(view != null)
            view.setController(controller);
        if(model != null){
            model.setView(view);
            model.updateViewAndModel();
        }
        return view;
    }
        
}
