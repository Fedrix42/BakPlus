package gui;
import gui.backups.incremental.IncrementalCreationController;
import gui.backups.integrity.IntegrityVerificationProgressController;
import gui.journal.rollback.JournalRollbackController;
import gui.mcvutils.InitMCV;
import gui.mainwindow.MainWindowController;
import gui.settings.SettingsController;
import gui.strategy.StrategyController;
import gui.strategy.management.StrategyManagementController;
import gui.strategy.creation.StrategyCreationController;


/**
 * Controller abstraction, it should hold the reference to the view which he is interacting with
 * @see diagram file for MVC pattern in this project
 * @author Fedrix
 */
public interface Controller {
    
    
    /**
     * Set the reference to the view
     * @param v the reference of the view the controller is interacting with
     * @see InitMCV class
     * @param <viewT> type of the view
     */
    public <viewT extends View> void setView(viewT v);
    
    /**
     * Set the reference to the model
     * @param m the reference of the model the controller is interacting with
     * @see InitMCV class
     * @param <modelT> type of the model
     */
    public <modelT extends Model> void setModel(modelT m);
    
    
    // Useful method for runtime casting
    
    default MainWindowController toMainWindowController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default SettingsController toSettingsController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyController toStrategyController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyCreationController toStrategyCreationController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyManagementController toStrategyManagementController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default IncrementalCreationController toIncrementalCreationController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default IntegrityVerificationProgressController toIntegrityVerificationProgressController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default JournalRollbackController toJournalRollbackController(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }    
    
    
    
}
