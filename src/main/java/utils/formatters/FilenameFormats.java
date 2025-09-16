package utils.formatters;

import core.strategy.Strategy;
import java.util.regex.Pattern;

/**
 * A global place to put filename formats
 * @author Fedrix
 */
public final class FilenameFormats {
    private static final String DATETIME_REGEX = "(?<datetime>" + FilenameDateTimeFormatter.DATETIME_REGEX  + ")";
    
    // Strategy
    public static final String STRATEGY_EXTENSION = ".stg";
    
    /** Format this string by inserting the strategy id to obtain the strategy filename */
    public static final String STRATEGY_FORMAT = "%d" + STRATEGY_EXTENSION; // StrategyID.stg
    
    public static final String STRATEGY_REGEX = Strategy.ID_REGEX + "(?<extension>\\.stg)";
    public static final Pattern STRATEGY_PATTERN = Pattern.compile(STRATEGY_REGEX);
    
    
    // Backup
    public static final String BACKUP_EXTENSION = ".zip";
    
    /** Format this string by inserting the strategy id and the formatted datetime to obtain the backup filename */
    public static final String BACKUP_FORMAT = "backup-%d-%s" + BACKUP_EXTENSION; // backup-strategyID-FilenameFormattedDateTimeHere.zip
    
    public static final String BACKUP_REGEX = "backup-" + Strategy.ID_REGEX + "-" + DATETIME_REGEX + "(?<extension>\\.zip)";
    public static final Pattern BACKUP_PATTERN = Pattern.compile(BACKUP_REGEX);
    
    
    
    // Journal
    // Formats
    public static final String JOURNAL_EXTENSION = ".journal";
    
    /** Format this string by inserting the strategy id to obtain the name of the journals folder*/
    public static final String JOURNAL_FOLDER_NAME = "%d-journals"; // StrategyID-journals
    
    /** Format this string by inserting the strategy id to obtain the filename of the complete journal of the strategy */
    public static final String JOURNAL_COMPLETE_TYPE = "%d" + JOURNAL_EXTENSION; // StrategyID.journal
    
    /** Format this string by inserting the strategy id to obtain the filename of a partial journal of the strategy */
    public static final String JOURNAL_PARTIAL_TYPE = "%d-%s" + JOURNAL_EXTENSION; // StrategyID-FilenameFormattedDateTimeHere.journal
        

    // Regex
    public static final String JOURNAL_FOLDER_NAME_REGEX = Strategy.ID_REGEX + "-journals";
    public static final String JOURNAL_COMPLETE_TYPE_REGEX = Strategy.ID_REGEX + "\\.journal";
    public static final String JOURNAL_PARTIAL_TYPE_REGEX = Strategy.ID_REGEX + "-" + DATETIME_REGEX + "\\.journal";
    // Patterns
    public static final Pattern JOURNAL_FOLDER_NAME_PATTERN = Pattern.compile(JOURNAL_FOLDER_NAME_REGEX);
    public static final Pattern JOURNAL_COMPLETE_TYPE_PATTERN = Pattern.compile(JOURNAL_COMPLETE_TYPE_REGEX);
    public static final Pattern JOURNAL_PARTIAL_TYPE_PATTERN = Pattern.compile(JOURNAL_PARTIAL_TYPE_REGEX);
    
    
}
