package gui.mainwindow;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import core.strategy.Strategy;
import core.strategy.StrategyManager;
import core.strategy.validation.Target;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import gui.View;
import gui.mainwindow.view.main.ColoredCellRenderer;
import gui.mainwindow.view.main.MainWindow;
import gui.mainwindow.view.main.MainWindowLabels;
import gui.Model;
import gui.mcvutils.GenericListModel;
import logging.LoggerManager;

/**
 * Model containing all objects used by main window to display informations
 * @author Fedrix
 */
public class MainWindowModel implements Model {
    private MainWindow view;
    private ColoredCellRenderer rendererForTargetsList;
    private final GenericListModel<Strategy> strategies;
    private final GenericListModel<Target> targets; // List of targets of loaded strategy
    private DefaultTreeModel destTreeModel; // Display all files in the destination folder of the currently loaded strategy
    
    public MainWindowModel(){
        destTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
        rendererForTargetsList = new ColoredCellRenderer();
        targets = new GenericListModel<Target>() {
            @Override
            public String elementToString(Target element) {
                return element.getPathString();
            }
        };
        strategies = new GenericListModel<Strategy>() {
            @Override
            public String elementToString(Strategy element) {
                return element.getName();
            }
        };
    }

    
    @Override
    public <viewT extends View> void setView(viewT v) {
        this.view = v.toMainWindow();
        view.setTip(MainWindowLabels.TIP.getMsg());
        view.setTargetsListModel(targets, rendererForTargetsList);
        view.setDestTreeModel(destTreeModel);
        view.setStrategiesListModel(strategies);
    }

    @Override
    public MainWindowModel toMainWindowModel() {
        return this;
    }

    
    
    @Override
    public void updateViewAndModel() {
        Strategy loaded = StrategyManager.getCurrentlyLoaded();
        if(loaded == null){
            view.setTip(MainWindowLabels.TIP.getMsg());
            targets.clear();
            view.setDestTreeModel(null);
            clearHighlight();
        } else {
            view.setTip(MainWindowLabels.LOADED_STRATEGY.getMsg(loaded.getName()));
            resetTargets();
            loadDestTree();
            view.setDestTreeModel(destTreeModel);
        }
        resetStrategies();
    }
    
    /**
     * 
     * @param index
     * @return the strategy at the index
     */
    public Strategy getStrategyAt(int index){
        return strategies.get(index);
    }
    
    /**
     * Clear and reload strategies from strategy manager
     */
    public void resetStrategies(){
        if(!this.strategies.getList().isEmpty())
            this.strategies.clear();
        if(StrategyManager.size() > 0){
            this.strategies.addAll(StrategyManager.getStrategiesCopy());
        }
    }
    
    /**
     * Clear and reload target of the currently loaded strategy
     */
    public void resetTargets() {
        Strategy loaded = StrategyManager.getCurrentlyLoaded();
        if(loaded != null){
            if(!this.targets.getList().isEmpty())
                this.targets.clear();
            this.targets.addAll(loaded.getTargetsCopy());
        }
    }
    
    /**
     * Load destination tree info into destTreeModel
     * requireNonNull: loaded
     */
    public void loadDestTree() {
        Strategy loaded = StrategyManager.getCurrentlyLoaded();
        if(loaded != null){
            // Creating tree and root element
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(loaded.getDst());
            destTreeModel = new DefaultTreeModel(root);

            // Filling subfolder of root with existing files
            try {
                Iterator<Path> it = Files.list(loaded.getDestinationFolder()).iterator();
                while(it.hasNext())
                    root.add(new DefaultMutableTreeNode(it.next().toString()));
            } catch (IOException ex){
                LoggerManager.exception().log(Level.SEVERE, "Couldn't load destination tree of a strategy.", ex);
            }

        }
    }
    
    public void highlightTarget(String target){
        rendererForTargetsList.addToHighLighted(target);
    }
    
    public void clearHighlight(){
        rendererForTargetsList.clearHighLighted();
    }
    
    
}
