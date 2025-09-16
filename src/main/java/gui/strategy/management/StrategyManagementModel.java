package gui.strategy.management;

import core.Compression;
import core.Encryption;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import gui.View;
import gui.strategy.creation.StrategyCreationModel;


public class StrategyManagementModel extends StrategyCreationModel {
    private StrategyManagementDialog view;
    private final StrategyComboBoxModel strategies; // String combo box
    
    
    public StrategyManagementModel(){
        super();
        strategies = new StrategyComboBoxModel();
    }
    
    
    @Override
    public void updateViewAndModel(){
        if(strategies.isSelected()){ // A strategy is selected
            Strategy selected = strategies.getSelectedItem();
            // Updating view info
            this.view.setStrategyNameValue(selected.getName());
            this.view.setDestFolder(selected.getDst());
            this.view.setStrategyID(selected.getId());

            // Updating model info
            super.setDestFolder(selected.getDst());
            super.encryption.setSelectedItem(selected.geteMode());
            super.compression.setSelectedItem(selected.getcMode());
            super.maxIncrementsModel.setValue(selected.getIncLimit());
            super.targets.clear();
            super.targets.addAll(selected.getTargetsCopy());
            super.filtersModel.clear();
            for(var filter : selected.getFiltersCopy()){
                filtersModel.addElement(filter);
            }
        } else { // No strategy is selected
            strategies.removeAllElements();
            if(StrategyManager.size() > 0){ // Some strategies remaining
                strategies.addAll(StrategyManager.getStrategiesCopy()); // Read references to the strategies, be careful
                strategies.setSelectedItem(strategies.getElementAt(0));
                this.updateViewAndModel();
            } else { // No strategies left
                // Updating view info
                this.view.setStrategyNameValue("");
                this.view.setDestFolder("");
                this.view.setStrategyID(0);
                
                // Updating model info
                super.setDestFolder("");
                super.targets.clear();
                super.filtersModel.clear();
                super.encryption.setSelectedItem(Encryption.values()[0]);
                super.compression.setSelectedItem(Compression.values()[0]);
                super.maxIncrementsModel.setValue(Strategy.MIN_INCREMENTAL_LIMIT);
            }
        } 
    }
    
    /**
     * Clear the selection of the strategy in the model.
     * Ex. After a strategy is deleted it should not be considered selected anymore
     */
    public void clearStrategySelection(){
        strategies.clearSelection();
    }
    
    /** @return the selected strategy */
    public Strategy getSelected(){
        return strategies.getSelectedItem();
    }
    
    
    
    
    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toStrategyManagementDialog();
        this.view.setStrategyComboBoxModel(strategies);
        this.view.setTargetsList(targets);
        this.view.setEncryptionComboBoxModel(encryption);
        this.view.setCompressionComboBoxModel(compression);
        this.view.setDestFolder(destFolder);
        this.view.setMaxIncrementsLimitSliderModel(maxIncrementsModel);
    }

    @Override
    public StrategyManagementModel toStrategyManagementModel() {
        return this;
    }
    
    
    
}
