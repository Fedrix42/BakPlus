package utils.formatters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Format date with filename ready strings
 * @author Fedrix
 */
public class FilenameDateFormatter {
    public static final String DATE_FORMAT = "dd_MMM_yyyy";
    public static final String DATE_REGEX = "[0-9]{2}_[a-zA-Z]+_[0-9]{4}";
    public static final Pattern DATE_PATTERN = Pattern.compile(DATE_REGEX);
    
    
    /**
     * Get a filename compatible date time as a string
     * @param date
     * @return 
     */
    public static String getFormatted(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
    
    /**
     * Get a filename compatible local date as a string of current time
     * @return 
     */
    public static String getFormattedNow(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
    
    /**
     * Reverse of getFormatted, parse a string produced by getFormatted to retrive a local date
     * @param formatted
     * @return 
     */
    public static LocalDateTime fromFormatted(String formatted){
        return LocalDateTime.parse(formatted, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
}
