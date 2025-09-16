package gui.backups.integrity;

import javax.swing.table.DefaultTableModel;
import gui.Model;
import gui.View;
import gui.backups.integrity.view.IntegrityVerificationProgressDialog;

/**
 * Model for journal data for integrity check
 * @author Fedrix
 */
public class IntegrityVerificationProgressModel implements Model {
    private IntegrityVerificationProgressDialog view;
    private final String[] header = new String[] {"Filename", "SHA256", "Passed", "Note"};
    private final Class[] typesArr = new Class[] {String.class, String.class, Boolean.class, String.class};
    private final DefaultTableModel tableModel;
    
    public IntegrityVerificationProgressModel(){
        tableModel = new DefaultTableModel(header, 0){
            @Override
            public Class getColumnClass(int columnIndex) {
                return typesArr[columnIndex];
            }
        };
    }
    
    
    public void addRow(String filename, String sha256, boolean verified, String note){
        tableModel.addRow(new Object[]{filename, sha256, verified, note});
    }
    
    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toIntegrityVerificationProgressDialog();
        view.setTableModel(tableModel);
    }

    @Override
    public IntegrityVerificationProgressModel toIntegrityVerificationProgressModel() {
        return this;
    }

    
    
    @Override
    public void updateViewAndModel() {}
    
}
