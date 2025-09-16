package gui.strategy.management;

import core.strategy.Strategy;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Fedrix
 */
public class StrategyComboBoxModel extends DefaultComboBoxModel<Strategy> {
    /**
     * Clear the selection of the strategy.
     */
    public void clearSelection(){
        super.setSelectedItem(null);
    }

    @Override
    public Strategy getSelectedItem() {
        return (Strategy) super.getSelectedItem();
    }
    
    /**
     * @return true if there is a strategy selected, false otherwise
     */
    public boolean isSelected(){
        return (super.getSelectedItem() != null);
    }
}
