package utils.formatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An easily human readable representation of time 
 * @author fedrix
 */
public class EasyDateTimeFormatter {
    public static final String DATETIME_FORMAT =  "dd MMM yyyy, HH::mm::ss";
    
    /**
     * Get an easily human readable representation of local date time 
     * @param time
     * @return 
     */
    public static String getFormatted(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
    }
}
