package eu.allan.quickcalendar.quickcalendar;

import android.content.Context;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by allanfrederiksen on 28/11/2017.
 */

public class QuickCalendar {
    private final Context context;


    private String theText;
    private String theDateString;
    private long theSeconds;

    public QuickCalendar(String str, Context context){
        this.context = context;
        this.theText = "";
        this.theDateString = "";
        this.theSeconds = 0;
        this.doSetSecondsAndString(str);
        if(theSeconds != 0){
            createDate(theSeconds*1000);
        }
    }

    private void createDate(long secs){

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        startMillis = beginTime.getTimeInMillis()+secs;
        Calendar endTime = Calendar.getInstance();
        endMillis = endTime.getTimeInMillis()+secs+1800000;

        Format dateFormat = android.text.format.DateFormat.getDateFormat(context);
        String pattern = ((SimpleDateFormat) dateFormat).toLocalizedPattern();
        //this.theDateString = pattern;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm " + pattern);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.theDateString = df.format(startMillis);
    }

    private void doSetSecondsAndString(String str){
        String[] splitStr = str.split("\\s+");
        String testString = splitStr[0];
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
            //System.out.println("Nummer " + i + " indeholder \"" + args[i] + "\".");
        }

        //System.out.println("num " + num);
        //System.out.println("secsModifier " + secsModifier);

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
        return theDateString + " " + theText;
    }
    public String getTheDateString() {
        return theDateString;
    }
    public long getTheSeconds() {
        return theSeconds;
    }

}