package tim.rguassessment.com.willthesunshineagain.db.Converters;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class Converters {


    @TypeConverter
    public static Calendar calendarFromTimestamp(Long value) {
        if (value != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(value * 1000L);
            return calendar;
        }
        else {
            return null;
        }
    }


    @TypeConverter
    public static Long calendarToTimestamp(Calendar calendar) {

        return calendar.getTimeInMillis();
    }


    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }


    @TypeConverter
    public static Long dateToTimestamp(Date date) {

        return date == null ? null : date.getTime();
    }
}
