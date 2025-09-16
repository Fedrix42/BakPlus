package gui;

import gui.backups.incremental.BackupCreationProgressDialog;
import gui.backups.integrity.view.BackupKeyInputDialog;
import gui.backups.integrity.view.IntegrityVerificationProgressDialog;
import gui.journal.JournalDataDialog;
import gui.journal.rollback.JournalRollbackDialog;
import gui.mainwindow.view.AboutDialog;
import gui.mainwindow.view.main.MainWindow;
import gui.settings.SettingsDialog;
import gui.strategy.creation.StrategyCreationDialog;
import gui.strategy.StrategyDialog;
import gui.strategy.filters.StrategyFiltersDialog;
import gui.strategy.management.StrategyManagementDialog;



/**
 * View abstraction: every frame, dialog, window an element which interact with a specific controller should implement a view
 * @see diagram file for MVC pattern in this project
 * @author Fedrix
 */
public interface View {
    public <T extends Controller> void setController(T controller);
    
    
    // Useful cast methods
    default StrategyCreationDialog toStrategyCreationDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyManagementDialog toStrategyManagementDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default SettingsDialog toSettingsDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default MainWindow toMainWindow(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default BackupCreationProgressDialog toBackupCreationProgressDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default IntegrityVerificationProgressDialog toIntegrityVerificationProgressDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default BackupKeyInputDialog toBackupKeyInputDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyDialog toStrategyDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default StrategyFiltersDialog toStrategyFiltersDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default JournalRollbackDialog toJournalRollbackDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default JournalDataDialog toJournalDataDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    default AboutDialog toAboutDialog(){
        throw new ClassCastException("This method is only callable by object which have a runtime type equal to the return type");
    }
    
}
