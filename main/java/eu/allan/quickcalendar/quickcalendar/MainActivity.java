package eu.allan.quickcalendar.quickcalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.CalendarContract.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;
    private Spinner calendarSpinner;
    private EditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            //requestContactsPermissions();
            this.askForPermission();

        } else {

            // Contact permissions have been granted. Show the contacts fragment.
            //showContactDetails();
            this.doCreateSpinner();
        }
        textInput = (EditText) findViewById(R.id.text_input);
        textInput.setImeActionLabel("Remind", KeyEvent.KEYCODE_ENTER);
        textInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    MainActivity.this.createReminder();
                    return true;
                }
                return false;
            }
        });
        textInput.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                TextView myOutputBox = (TextView) findViewById(R.id.text_output);
                QuickCalendar qc = new QuickCalendar(s + "", MainActivity.this);
                myOutputBox.setText(qc.getTheTextAndDate());

            }
        });
    }

    private void createReminder() {
        System.out.println("createReminder");
        QuickCalendar qc = new QuickCalendar(this.textInput.getText().toString(), MainActivity.this);
        Toast.makeText(MainActivity.this, qc.getTheDateString() + "\n" + qc.getTheText(), Toast.LENGTH_LONG).show();

        long calID = getCalendarId(String.valueOf(calendarSpinner.getSelectedItem()));
        long startMillis = qc.getTheSeconds()*1000;
        long endMillis = qc.getTheSeconds()*1800*1000;
/*        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.getTimeInMillis();*/


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis+Calendar.getInstance().getTimeInMillis());
        values.put(Events.DTEND, endMillis+Calendar.getInstance().getTimeInMillis());
        values.put(Events.TITLE, qc.getTheText());
        values.put(Events.DESCRIPTION, "Quick Reminder");
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, String.valueOf(TimeZone.getDefault()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("RETURN");
            return;
        }
        Uri uri = cr.insert(Events.CONTENT_URI, values);

// get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        System.out.println("eventID: " + eventID);
        System.out.println("calID: " + calID);
//
// ... do something with event ID
//
//

    }
    private long getCalendarId(String calName) {
        long retval = 1;
        String selection = "(" + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ?)";

        String[] selectionArgs = new String[]{
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_OWNER),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_EDITOR),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_ROOT)};


        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,

        };

        final ContentResolver cr = getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        @SuppressLint("MissingPermission") Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        while (cur.moveToNext()) {
        /* do something with the cursor:
           Long id = cur.getLong(0);
           String name = cur.getString(1);
           int color = cur.getInt(2)));
        */
            if(cur.getString(1).equals(calName)){
                retval = cur.getLong(0);
            }
        }
        return retval;
    }
    private void doCreateSpinner() {
        String selection = "(" + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ? OR "
                + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " =  ?)";

        String[] selectionArgs = new String[]{
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_OWNER),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_EDITOR),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR),
                Integer.toString(CalendarContract.Calendars.CAL_ACCESS_ROOT)};


        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.IS_PRIMARY
        };
        //Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, selection, selectionArgs, null);
        final ContentResolver cr = getContentResolver();
        final Uri uri = CalendarContract.Calendars.CONTENT_URI;
        @SuppressLint("MissingPermission") Cursor cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        List<String> list = new ArrayList<String>();
        while (cur.moveToNext()) {
        /* do something with the cursor:
           Long id = cur.getLong(0);
           String name = cur.getString(1);
           int color = cur.getInt(2)));
        */
            list.add(cur.getString(1));
        }

        this.calendarSpinner = (Spinner) findViewById(R.id.cal_spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        calendarSpinner.setAdapter(dataAdapter);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        //String defaultValue = getResources().getString(R.string.lastUsedCalendar);
        String lastUsedCalendar = sharedPref.getString(getString(R.string.lastUsedCalendar), "");

        calendarSpinner.setSelection(dataAdapter.getPosition(lastUsedCalendar));
        calendarSpinner.setOnItemSelectedListener(this);

    }

    private void askForPermission(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Kalenderadgang godkendt.
                    this.doCreateSpinner();

                } else {
                    // Ingen adgang til kalenderen
                    /*Toast.makeText(MainActivity.this,
                            "NEED CALENDAR ACCESS",
                            Toast.LENGTH_LONG).show();
                    this.finish();*/
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(MainActivity.this, "Calendar:\n"+ String.valueOf(calendarSpinner.getSelectedItem()), Toast.LENGTH_LONG).show();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.lastUsedCalendar), String.valueOf(calendarSpinner.getSelectedItem()));
        editor.commit();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
