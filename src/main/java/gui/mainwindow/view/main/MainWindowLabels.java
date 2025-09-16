package gui.mainwindow.view.main;



/**
 * Labels of main window
 * @author Fedrix
 */
public enum MainWindowLabels {
    TIP("Load a strategy or create a new one to start"),
    LOADED_STRATEGY("Loaded Strategy: %s");
    
    private final String msg;
    MainWindowLabels(String msg){
        this.msg = msg;
    }
    public String getMsg(Object... args){
        if(args.length != 0)
            return msg.formatted(args);
        else 
            return msg;
    }
}
