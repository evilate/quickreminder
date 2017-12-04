package eu.allan.quickcalendar.quickcalendar;

import android.content.Context;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by allanfrederiksen on 28/11/2017.
 */

public class QuickCalendar {
    private final Context context;


    private String theText;
    private String theDateAndTimeString;
    private String theDateString;
    private String theTimeString;
    private long theSeconds;
    private long theSecondsDefault = 600;

    public QuickCalendar(String str, Context context){
        this.context = context;
        this.theText = "";
        this.theDateAndTimeString = "";
        this.theSeconds = 0;
        this.doSetSecondsAndString(str);
        if(theSeconds == 0){
            createDate(theSecondsDefault*1000);
        }else{
            createDate(theSeconds*1000);
        }

    }

    private void createDate(long secs){

        long startMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        startMillis = beginTime.getTimeInMillis()+secs;
        Format dateFormat = android.text.format.DateFormat.getDateFormat(context);
        //String pattern = ((SimpleDateFormat) dateFormat).;
        String pattern = ((SimpleDateFormat) dateFormat).toLocalizedPattern();
        //String pattern = DateFormat.getDateTimeInstance().format(startMillis+offsetFromUtc);
        //SimpleDateFormat df = new SimpleDateFormat("HH:mm " + pattern);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm " + pattern);
        //df.setTimeZone(TimeZone.getTimeZone("GMT"));
        //TimeZone tz = TimeZone.getDefault();
        //Date now = new Date();
        //long offsetFromUtc = tz.getOffset(now.getTime());
        //offsetFromUtc = 0;
        //System.out.println("offsetFromUtc: " + offsetFromUtc);
        //this.theDateAndTimeString = df.format((startMillis+offsetFromUtc));
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        Date now = new Date();
        long offsetFromUtc = 0;//tz.getOffset(now.getTime());

        this.theDateAndTimeString = df.format((startMillis+offsetFromUtc));


        //now.setTime(startMillis+offsetFromUtc);
        //df = new SimpleDateFormat("HH:mm d. MMM. yyyy");
        //this.theDateAndTimeString = df.format((startMillis+offsetFromUtc));

        df = new SimpleDateFormat("d. MMM yyyy");
        this.theDateString = df.format((startMillis+offsetFromUtc));
        df = new SimpleDateFormat("HH:mm");
        this.theTimeString = df.format((startMillis+offsetFromUtc));
        System.out.println("theTimeString: " + theTimeString);
    }

    private void doSetSecondsAndString(String str){
        str = str.replaceAll("\n", "");
        String[] splitStr = str.split("\\s+");
        String testString;
        if(splitStr.length > 0){
            testString = splitStr[0];
        }else{
            testString = str;
        }

        String num = "";
        String modifier = "";
        for(int i = 0; i < testString.length(); i++){

            char c = testString.charAt(i);
            if(isDigit(c)){
                num += c;
            }else{
                modifier += c;
            }
        }

        int numResult;
        long secsModifier;
        try {
            numResult = Integer.parseInt(num);
            secsModifier = getSecondsFromModidier(modifier);
            theSeconds = numResult * secsModifier;
        } catch (NumberFormatException e) {
            numResult = 0;
            secsModifier = 0L;
            theSeconds = numResult * secsModifier;
        }

        int n = 0;
        if(secsModifier != 0){
            n = 1;
        }
        for(int i = n;i < splitStr.length; i++){
            this.theText += splitStr[i] + " ";
        }
    }


    private long getSecondsFromModidier(String m){
        long l = 0L;
        if(m.equals("m")){
            l = 60;
        }else if(m.equals("h")){
            l = 60*60;
        }else if(m.equals("d")){
            l = 60*60*24;
        }else if(m.equals("w")){
            l = 60*60*24*7;

        }
        return l;
    }
    private static boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private static boolean isLetterOrDigit(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9');
    }

    public String getTheText() {
        return theText;
    }
    public String getTheTextAndDate() {
        return theDateAndTimeString + " " + theText;
    }
    public String getTheDateAndTimeString() {
        return theDateAndTimeString;
    }
    public long getTheSeconds() {
        long retval = theSeconds;
        if(theSeconds == 0){
            retval = theSecondsDefault;
        }
        return retval;
    }

    public String getTheDateString() {
        return theDateString;
    }

    public String getTheTimeString() {
        String retval = theTimeString;
        if(this.theSeconds == 0){
            retval = theTimeString + " (" + (this.theSecondsDefault/60) + " minutes default)";
        }
        return retval;
    }
}