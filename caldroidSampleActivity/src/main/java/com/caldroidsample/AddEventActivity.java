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
import java.io.IOException;

/*
*   Created by
*   Fransiscus Stephen
 */

public class AddEventActivity extends AppCompatActivity {

    File file;
    String filename = "EventData.txt";
    String contents;
    DataProcess dataProcess;

    /*  Initialize the first set up
    * -. Create DataProcess class and get the data
    * -. Get the data from MainActivity Intent
    * -. Display selected date into form
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        file = new File(getFilesDir(),filename);
        dataProcess = new DataProcess(getApplicationContext(),file,filename);

        Intent intent = getIntent();
        long date = intent.getLongExtra(MainActivity.SelectedDate, 0);

        setContentView(R.layout.activity_add_event);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        else {
            contents = dataProcess.getData();
        }

        DatePicker dpFrom = (DatePicker)findViewById(R.id.datePickerFrom);
        DatePicker dpTo = (DatePicker)findViewById(R.id.datePickerTo);

        dpFrom.setMinDate(date);
        dpTo.setMinDate(date);

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(saveEvent);
    }

    // Function button listener for save event button
    private View.OnClickListener saveEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveData();
        }
    };

    /* Function to save the data
    *   -. Get the input data
    *   -. Call saveData function from DataProcess Class
    * */
    public void saveData(){
        try {
            FileOutputStream out = openFileOutput(filename, MODE_APPEND);

            EditText evName = (EditText)findViewById(R.id.eventName);
            DatePicker dpFrom = (DatePicker)findViewById(R.id.datePickerFrom);
            TimePicker tpFrom = (TimePicker)findViewById(R.id.timePickerFrom);
            DatePicker dpTo = (DatePicker)findViewById(R.id.datePickerTo);
            TimePicker tpTo = (TimePicker)findViewById(R.id.timePickerTo);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            boolean flagSave = dataProcess.saveData(alertDialog, out, evName.getText().toString(), dpFrom, tpFrom, dpTo, tpTo);
            if(flagSave == true){
                finish();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
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
