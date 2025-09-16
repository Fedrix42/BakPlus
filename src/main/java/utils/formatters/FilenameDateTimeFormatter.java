package utils.formatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;



/**
 * A filename compatible way to format local date time
 * @author Fedrix
 */
public class FilenameDateTimeFormatter extends FilenameDateFormatter {
    public static final String DATETIME_FORMAT = DATE_FORMAT + "_HH_mm_ss";
    public static final String DATETIME_REGEX = DATE_REGEX + "_[0-9]{2}_[0-9]{2}_[0-9]{2}";
    public static final Pattern DATETIME_PATTERN = Pattern.compile(DATETIME_REGEX);
    
    /**
     * Get a filename compatible local date time as a string
     * @param time
     * @return 
     */
    public static String getFormatted(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }
    
    /**
     * Get a filename compatible local date time as a string of current time
     * @return 
     */
    public static String getFormattedNow(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }
    
    /**
     * Reverse of getFormatted, parse a string produced by getFormatted to retrive a local date time
     * @param formatted
     * @return 
     */
    public static LocalDateTime fromFormatted(String formatted){
        return LocalDateTime.parse(formatted, DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }
}
