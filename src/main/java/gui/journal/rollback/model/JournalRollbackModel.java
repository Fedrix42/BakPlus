package gui.journal.rollback.model;

import core.journal.Journal;
import core.journal.manager.JournalIOManager;
import core.journal.manager.JournalManager;
import core.journal.manager.JournalRollbackManager;
import core.strategy.Strategy;
import gui.Model;
import gui.View;
import gui.journal.rollback.JournalRollbackDialog;
import java.io.IOException;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import logging.LoggerManager;
import logging.notifications.ExceptionNotification;
import logging.notifications.NotificationManager;

/**
 *
 * @author fedrix
 */
public class JournalRollbackModel implements Model  {
    private JournalRollbackDialog view;
    private final JournalListModel journals;
    private final Strategy cls;
    
    public JournalRollbackModel(Strategy cls){
        this.cls = requireNonNull(cls);
        journals = new JournalListModel();
    }
    
    public void rollback() throws IOException {
        JournalRollbackManager.rollback(journals.getJournalAt(view.getSelectedIndex()).getCreatedTime(), cls.getId());
    }
    
    public Journal getSelectedJournal(){
        return journals.getJournalAt(view.getSelectedIndex());
    }

    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toJournalRollbackDialog();
        view.setJournalsListModel(journals);
    }

    @Override
    public void updateViewAndModel() {
        journals.clear();
        try {
            journals.addAll(JournalRollbackManager.getAllPartialJournals(cls.getId()));
        } catch (IOException | ClassNotFoundException ex) {
            var not = new ExceptionNotification("Coudldn't get partial journals", ex, Level.WARNING);
            LoggerManager.exceptionNL().log(not);
            NotificationManager.notify(view, not);
        }
        journals.sort((Journal j1, Journal j2) -> {
            return j2.getCreatedTime().compareTo(j1.getCreatedTime());
        });
    }

    @Override
    public JournalRollbackModel toJournalRollbackModel() {
        return this;
    }
    
}
