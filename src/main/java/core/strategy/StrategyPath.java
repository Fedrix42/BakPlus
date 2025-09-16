package core.strategy;

import java.nio.file.Path;
import settings.SettingsManager;
import utils.formatters.FilenameFormats;


public class StrategyPath {
    
    /**
     * @param stg
     * @return return the path of a file rappresenting a strategy in file system
     */
    public static Path get(Strategy stg){
        return get(stg.getId());
    }
    
    /**
     * @param strategyID
     * @return return the path of a file rappresenting a strategy in file system
     */
    public static Path get(long strategyID){
        Path result = SettingsManager.getCurrent().getStrategiesFolderPath().resolve(FilenameFormats.STRATEGY_FORMAT.formatted(strategyID));
        return result;
    }
    
}
