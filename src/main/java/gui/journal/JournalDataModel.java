package gui.journal;

import core.journal.Journal;
import core.journal.JournalEntry;
import gui.Model;
import gui.View;
import java.util.Iterator;
import javax.swing.table.DefaultTableModel;
import utils.Pair;

/**
 * A model containing journal data which can be used for @see JournalDataDialog
 * @author fedrix
 */
public class JournalDataModel implements Model {
    private JournalDataDialog view;
    private final String[] header = new String[] {"Filename", "SHA256", "Files with this filename"};
    private final Class[] typesArr = new Class[] {String.class, String.class, Integer.class};
    private final DefaultTableModel tableModel;
    
    
    public JournalDataModel(Journal journal){
        tableModel = new DefaultTableModel(header, 0){
            @Override
            public Class getColumnClass(int columnIndex) {
                return typesArr[columnIndex];
            }
        };
        Iterator<JournalEntry> it = journal.iterator();
        while(it.hasNext()){
            JournalEntry data = it.next();
            tableModel.addRow(new Object[]{data.filename(), data.checksum().lastToString(), data.numOfFiles()});
        }
    }
    

    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toJournalDataDialog();
        view.setTableModel(tableModel);
    }

    @Override
    public void updateViewAndModel() {}

    @Override
    public JournalDataModel toJournalDataModel() {
        return this;
    }
    
    
    
    
}
