package com.caldroidsample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*
*   Created by
*   Fransiscus Stephen
 */

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity {
    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;

    public final static String SelectedDate = "Selected_Date";
    public final static String SelectedItem = "Selected_Item";
    private Button btnAddEvent;
    DataProcess dataProcess;

    File file;
    String filename = "EventData.txt";
    public String contents;
    Date selectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = new File(getFilesDir(),filename);
        dataProcess = new DataProcess(getApplicationContext(),file,filename);

        btnAddEvent = (Button) findViewById(R.id.btnAddEvent);
        btnAddEvent.setOnClickListener(clickAddEvent);

        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(),"Created",Toast.LENGTH_SHORT).show();
        }
        else {
            contents = dataProcess.getData();
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");

        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // Setup arguments

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            caldroidFragment.setSelectedDate(cal.getTime());
            caldroidFragment.setArguments(args);
        }

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                selectDate = date;
                String dateString = formatter.format(date);
                String[] arrDate = dateString.split(" ");
                setEventDataEachDay(Integer.parseInt(arrDate[2]), Integer.parseInt(arrDate[1]), Integer.parseInt(arrDate[0]));
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                setEventData();
                Toast.makeText(getApplicationContext(),
                        "All Event Showed! ",
                        Toast.LENGTH_SHORT).show();
            }


        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    private View.OnClickListener clickAddEvent = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
                intent.putExtra(SelectedDate, selectDate.getTime());
                startActivity(intent);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"Select a date first",Toast.LENGTH_SHORT).show();
            }
        }
    };


    // Show all events
    public void setEventData(){
        ListView lvEvent = (ListView) findViewById(R.id.listEvent);
        ArrayAdapter adapter;
        if(!contents.equals("")) {
            String[] temp = contents.split("\n");
            adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, temp);
            lvEvent.setAdapter(adapter);
            lvEvent.setOnItemClickListener(itemClickListener);
        }
        else {
            String[] temp = {"Empty"};
            adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, temp);
            lvEvent.setAdapter(adapter);
        }
    }

    // Show event at certain day
    public void setEventDataEachDay(int year, int month, int day){
        ListView lvEvent = (ListView) findViewById(R.id.listEvent);
        String[] data,temp;
        data = new String[]{"-1"};
        int count = 0;
        ArrayAdapter adapter;
        if(!contents.equals("")) {
            temp = contents.split("\n");
            for(int i=0;i<temp.length;i++){
                int yearData = dataProcess.getFromYearAt(i);
                int monthData = dataProcess.getFromMonthAt(i);
                int dayData = dataProcess.getFromDayAt(i);
                String[] dat = temp[i].split(";");
                dat[1] = "\nStart date = " + dat[1];
                dat[2] = "\nEnd date = " + dat[2];
                String textEvent = dat[0] + dat[1] + dat[2];
                if(year == yearData && month == monthData && day == dayData){
                    data[count] = textEvent;
                    count++;
                }
            }
            if(!data[0].equals("-1")){
                adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, data);
                lvEvent.setAdapter(adapter);
                lvEvent.setOnItemClickListener(itemClickListener);
            }
            else{
                temp = new String[]{"Empty"};
                adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, temp);
                lvEvent.setAdapter(adapter);
            }
        }
        else {
            temp = new String[]{"Empty"};
            adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, temp);
            lvEvent.setAdapter(adapter);
        }
    }

    // Listener listview for showing edit event activity
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView tv = (TextView) view.findViewById(R.id.label);
            Intent intent = new Intent(getApplicationContext(), EditEventActivity.class);
            intent.putExtra(SelectedItem,position);
            startActivity(intent);
        }
    };

    // Function to set the mark on Calendar
    public void setMarkEvent(){
        if(!contents.equals("")) {
            String[] temp = contents.split("\n");
            for(int i=0;i<temp.length;i++){
                String[] data = dataProcess.getDataAt(i);
                SimpleDateFormat sdff = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date dd = null;
                try {
                    dd = sdff.parse(data[1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_light_red,dd);
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(file.exists()) {
                contents = dataProcess.getData();
                setMarkEvent();
                caldroidFragment.refreshView();
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
