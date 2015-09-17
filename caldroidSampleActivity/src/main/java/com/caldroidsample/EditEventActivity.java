package com.caldroidsample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
*   Created by
*   Fransiscus Stephen
 */

public class EditEventActivity extends AppCompatActivity {

    DataProcess dataProcess;
    File file;
    String filename = "EventData.txt";
    String contents;
    AlertDialog.Builder builder;
    ArrayList<String[]> alData;
    int position;

    /*  Initialize the first set up
    * -. Create DataProcess class and get the data
    * -. Get the data from MainActivity Intent
    * -. Display selected data into edit form
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        file = new File(getFilesDir(),filename);
        dataProcess = new DataProcess(getApplicationContext(),file,filename);
        contents = dataProcess.getData();

        Intent intent = getIntent();
        position = intent.getIntExtra(MainActivity.SelectedItem, 0);

        setContentView(R.layout.activity_edit_event);
        builder = new AlertDialog.Builder(this);

        String[] data;

        data = dataProcess.getDataAt(position);

        DatePicker dpFrom = (DatePicker)findViewById(R.id.editDatePickerFrom);
        TimePicker tpFrom = (TimePicker)findViewById(R.id.editTimePickerFrom);
        DatePicker dpTo = (DatePicker)findViewById(R.id.editDatePickerTo);
        TimePicker tpTo = (TimePicker)findViewById(R.id.editTimePickerTo);

        Date dateFrom,dateTo;

        try {
            dateFrom = getDateFromString(data[1]);
            dateTo = getDateFromString(data[2]);

            dpFrom.setMinDate(dateFrom.getTime());
            dpTo.setMinDate(dateTo.getTime());

            dpFrom.updateDate(dataProcess.getFromYearAt(position),dataProcess.getFromMonthAt(position),dataProcess.getFromDayAt(position));
            dpTo.updateDate(dataProcess.getToYearAt(position),dataProcess.getToMonthAt(position),dataProcess.getToDayAt(position));

            tpFrom.clearFocus();
            tpTo.clearFocus();

            tpFrom.setCurrentHour(dateFrom.getHours());
            tpFrom.setCurrentMinute(dateFrom.getMinutes());
            tpTo.setCurrentHour(dateTo.getHours());
            tpTo.setCurrentMinute(dateFrom.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Button editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(editEvent);
        Button deleteButton = (Button) findViewById(R.id.editDeleteButton);
        deleteButton.setOnClickListener(deleteEvent);
    }

    // change String data with format "dd/MM/yyyy HH:mm" to Date data
    public Date getDateFromString(String currDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d;
        d = sdf.parse(currDate);
        return d;
    }

    // Function listener for edit button
    private View.OnClickListener editEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            editData();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    };

    // Function to edit selected data
    public void editData(){
        try {
            FileOutputStream out = openFileOutput(filename,MODE_PRIVATE);

            EditText evName = (EditText)findViewById(R.id.editEventName);
            DatePicker dpFrom = (DatePicker)findViewById(R.id.editDatePickerFrom);
            TimePicker tpFrom = (TimePicker)findViewById(R.id.editTimePickerFrom);
            DatePicker dpTo = (DatePicker)findViewById(R.id.editDatePickerTo);
            TimePicker tpTo = (TimePicker)findViewById(R.id.editTimePickerTo);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            boolean flagEdit = dataProcess.editDataAt(alertDialog, out, position, evName.getText().toString(), dpFrom, tpFrom, dpTo, tpTo);

            if(flagEdit == true) {
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    // Function button listener for delete button
    private View.OnClickListener deleteEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            deleteData();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            Toast.makeText(getApplicationContext(),"Canceled",Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener2)
                    .setNegativeButton("No", dialogClickListener2).show();
        }
    };

    // Function for delete the selected data
    public void deleteData(){
        try {
            FileOutputStream out = openFileOutput(filename,MODE_PRIVATE);
            dataProcess.deleteDataAt(out,position);
            finish();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
