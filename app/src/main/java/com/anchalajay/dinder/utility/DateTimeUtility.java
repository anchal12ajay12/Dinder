package com.anchalajay.dinder.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtility {

    public static String getFormattedDate(long time_stamp){
        Date date = new Date(time_stamp);
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd/MM/yy", Locale.forLanguageTag("en"));
        return formatter.format(date);
    }
}
