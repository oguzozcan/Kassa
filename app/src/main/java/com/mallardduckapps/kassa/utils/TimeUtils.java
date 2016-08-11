package com.mallardduckapps.kassa.utils;

import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Locale;

/**
 * Created by oguzemreozcan on 08/08/16.
 */
public class TimeUtils {
    public final static Locale localeTr = new Locale("tr");
    //public final static DateTimeFormatter dtfIn = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    public final static DateTimeFormatter updateDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    public final static DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd MMMM yyyy HH:mm").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    public final static DateTimeFormatter dtfOutWOTime = DateTimeFormat.forPattern("dd MMMM yyyy").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    private final static DateTimeFormatter dtfSimple = DateTimeFormat.forPattern("dd.MM.yyyy HH.mm").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    public final static DateTimeFormatter dtfSimpleWOTime = DateTimeFormat.forPattern("dd.MM.yyyy").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    private final static DateTimeFormatter timeSimple = DateTimeFormat.forPattern("HH.mm");
    public final static DateTimeFormatter timeReadable = DateTimeFormat.forPattern("HH:mm");
    public final static DateTimeFormatter dtfOutWOYear = DateTimeFormat.forPattern("dd MMMM HH:mm").withLocale(localeTr).withZone(DateTimeZone.forID("Europe/Istanbul"));
    public static final DateTimeFormatter dfISO = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withChronology(ISOChronology.getInstanceUTC());//.withLocale(TimeUtil.localeTr)
    //.withChronology(ISOChronology.getInstanceUTC()); //  //,SSS
    public static final DateTimeFormatter dfISOMS = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withChronology(ISOChronology.getInstanceUTC()); // 'Z'
    //public static DurationFieldType[] fields = {DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes()};
    private static final String TAG = "TimeUtil";
    //.withLocale(TimeUtil.localeTr).withChronology(ISOChronology.getInstanceUTC())


    public static String calculateTimeInBetween(String date) {
        Log.d(TAG, "Date: " + date);
        if(date == null){
            return "";
        }
        DateTime startDate = TimeUtils.getDateTime(date, TimeUtils.dfISO);//DateTime.now(); // now() : since Joda Time 2.0
        DateTime endDate = DateTime.now();//new DateTime(2011, 12, 25, 0, 0);

        Period period = new Period(endDate, startDate, PeriodType.dayTime());//MonthDayTime());
        period.normalizedStandard(PeriodType.dayTime());
        PeriodFormatter formatter = new PeriodFormatterBuilder()
//                .appendYears().appendSuffix(" year ", " years ")
//                .appendMonths().appendSuffix(" month ", " months ")
                .appendDays().appendSuffix(" GÜN ", " GÜN ")
                .appendHours().appendSuffix(" SA ", " SA ")
                //.appendMinutes().appendSuffix(" minute ", " D ")
//                .appendSeconds().appendSuffix(" second ", " seconds ")
                .toFormatter();

        Log.d(TAG, formatter.print(period));
        return formatter.print(period);
    }

    public static String getTodayJoda(boolean humanReadable) {
        DateTime dt = DateTime.now();
        if (humanReadable) {
            return dtfOut.print(dt);
        }
        return updateDateFormat.print(dt);
    }

    public static String convertDateTimeFormat(DateTimeFormatter fromFormat, DateTimeFormatter toFormat, String date) {
        if(date == null){
            return "";
        }
        DateTime dt;
        try{
            dt = fromFormat.parseDateTime(date).toDateTime();
        }catch(IllegalArgumentException ex){
            ex.printStackTrace();
            return date;
        }
        //LocalDateTime localDateTime = dt.toLocalDateTime();
        try {
            return dt.toString(toFormat);//toFormat.print(fromFormat.parseDateTime(date));
        } catch (Exception e) {
            if (fromFormat == TimeUtils.dfISO) {
                Log.e(TAG, "EXCEPTION ON TIME : ");
                return dt.toString(TimeUtils.dfISOMS);//toFormat.print(TimeUtil.dfISOMS.parseDateTime(date));
            } else {
                Log.e(TAG, "EXCEPTION ON TIME : ");
                e.printStackTrace();
                return date;
            }
        }
    }

    public static DateTime getDateTime(String dateTimeText, boolean humanReadable) {
        DateTime dt;
        if (humanReadable) {
            dt = dtfOut.parseDateTime(dateTimeText).toDateTime();
        } else {
            dt = dtfSimple.parseDateTime(dateTimeText).toDateTime();
        }
        return dt;
    }

    public static DateTime getDateTime(String dateTimeText, DateTimeFormatter dtf) {
        try {
            return dtf.parseDateTime(dateTimeText).toDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return dfISOMS.parseDateTime(dateTimeText).toDateTime();
        }
    }

    public static String convertSimpleDateToReadableForm(String date, boolean isTimeIncluded) {
        return isTimeIncluded ? dtfOut.print(dtfSimple.parseDateTime(date)) :
                dtfOutWOTime.print(dtfSimpleWOTime.parseDateTime(date));
    }

    public static String convertSimpleDateToApiForm(String date, boolean isTimeIncluded) {
        return isTimeIncluded ? dfISOMS.print(dtfOut.parseDateTime(date)) :
                dfISOMS.print(dtfOutWOTime.parseDateTime(date));
    }

    public static String convertSimpleTimeToReadableForm(String time) {
        return timeReadable.print(timeSimple.parseDateTime(time));
    }

    public static String convertReadableDateToSimpleForm(String date) {
        return dtfSimple.print(dtfOut.parseDateTime(date));
    }

    public static String getDaysBeforeOrAfterToday(int days, boolean humanReadable) {
        DateTime dateTime = DateTime.now();
        if (days > 0) {
            dateTime = dateTime.plusDays(days);
        } else {
            dateTime = dateTime.minusDays(-1 * days);
        }
        if (humanReadable) {
            return dtfOut.print(dateTime);
        }
        return updateDateFormat.print(dateTime);
    }
}
