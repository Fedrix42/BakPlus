package gui;

import gui.backups.integrity.IntegrityVerificationProgressModel;
import gui.journal.rollback.model.JournalRollbackModel;
import gui.mcvutils.InitMCV;
import gui.journal.JournalDataModel;
import gui.mainwindow.MainWindowModel;
import gui.settings.SettingsDialogModel;
import gui.strategy.creation.StrategyCreationModel;
import gui.strategy.management.StrategyManagementModel;

/**
 * A generic model contains info used by views.
 * It is controlled by a controller.
 * @see diagram file for MVC pattern in this project
 * @author Fedrix
 */
public interface Model {
    /**
     * Set the reference to the view
     * @param v the reference of the view the controller is interacting with
     * @see InitMCV class
     * @param <viewT> type of the view
     */
    public <viewT extends View> void setView(viewT v);
    public void updateViewAndModel();
    
    
    //Useful cast methods
    
    default IntegrityVerificationProgressModel toIntegrityVerificationProgressModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default MainWindowModel toMainWindowModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default SettingsDialogModel toSettingsModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyCreationModel toStrategyCreationModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyManagementModel toStrategyManagementModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }  
    default JournalRollbackModel toJournalRollbackModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default JournalDataModel toJournalDataModel(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }    
    
}
