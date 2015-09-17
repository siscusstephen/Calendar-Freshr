package com.caldroidsample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by Stephen on 10/09/2015.
 */
public class DataProcess {

    File file;
    String filename;
    Context context;
    ArrayList<String[]> alData; // Contain event data in String[] format

    private String contents; // Contain event data in String format

    // Constructor
    public DataProcess(Context g, File f, String fname){
        context = g;
        file = f;
        filename = fname;
    }

    // Function to write the String data into File
    public void writeToFile(FileOutputStream fos, String data){
        try {
            FileOutputStream out = fos;
            out.write(data.getBytes());
            out.close();
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    // Function to save the data into file text
    public boolean saveData(AlertDialog alrtDialog, FileOutputStream fos, String evName, DatePicker dpF, TimePicker tpF, DatePicker dpT, TimePicker tpT){
        boolean flagSave = false;
        AlertDialog alertDialog = alrtDialog;
        alertDialog.setTitle("Alert");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        try {
            FileOutputStream out = fos;

            String eventName = evName;
            DatePicker dpFrom = dpF;
            DatePicker dpTo = dpT;
            TimePicker tpFrom = tpF;
            TimePicker tpTo = tpT;

            tpFrom.clearFocus();
            tpTo.clearFocus();

            int fromHour = tpFrom.getCurrentHour();
            int fromMin = tpFrom.getCurrentMinute();
            int toHour = tpTo.getCurrentHour();
            int toMin = tpTo.getCurrentMinute();

            String fDate,tDate;
            fDate = String.valueOf(dpFrom.getDayOfMonth()) + "/" + String.valueOf(dpFrom.getMonth()+1) + "/" + String.valueOf(dpFrom.getYear()) + " " + Integer.toString(fromHour) + ":" + Integer.toString(fromMin);
            tDate = String.valueOf(dpTo.getDayOfMonth()) + "/" + String.valueOf(dpTo.getMonth()+1) + "/" + String.valueOf(dpTo.getYear()) + " " + Integer.toString(toHour) + ":" + Integer.toString(toMin);

            long fromDate,toDate;
            fromDate = getLong(fDate);
            toDate = getLong(tDate);

            flagSave = validateDate(alertDialog, fromDate, toDate, fromHour, fromMin, toHour, toMin);

            if(flagSave == true){
                boolean chkOverlap;
                chkOverlap = checkNotOverlap(fromDate,toDate,-1);

                if(chkOverlap == true){
                    String data = eventName + ";" + fDate + ";" + tDate + "\n";
                    writeToFile(out,data);
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    flagSave = false;
                    alertDialog.setMessage("Can't put multiple event in the same time");
                    alertDialog.show();
                }
            }
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        return flagSave;
    }

    // Function to get all the event data
    public String getData(){
        int length = (int) file.length();
        byte[] bytes = new byte[length];
        try{
            FileInputStream in = new FileInputStream(file);

            in.read(bytes);
            in.close();
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        contents = new String(bytes);
        return contents;
    }

    // Function to get 1 event data at certain position in the arraylist
    public String[] getDataAt(int position){
        String[] data;

        setArrayList();
        data = alData.get(position);

        return data;
    }

    // Function to get the "From" year data from the saved data
    public int getFromYearAt(int position){
        String data[] = getDataAt(position);
        int yearAt=0;

        String[] date = data[1].split(" ");
        String[] year = date[0].split("/");

        yearAt = Integer.parseInt(year[2]);

        return yearAt;
    }

    // Function to get the "From" month data from the saved data
    public int getFromMonthAt(int position){
        String data[] = getDataAt(position);
        int monthAt=0;

        String[] date = data[1].split(" ");
        String[] month = date[0].split("/");

        monthAt = Integer.parseInt(month[1]);

        return monthAt;
    }

    // Function to get the "From" day data from the saved data
    public int getFromDayAt(int position){
        String data[] = getDataAt(position);
        int dayAt=0;

        String[] date = data[1].split(" ");
        String[] day = date[0].split("/");

        dayAt = Integer.parseInt(day[0]);

        return dayAt;
    }

    // Function to get the "To" year data from the saved data
    public int getToYearAt(int position){
        String data[] = getDataAt(position);
        int yearAt=0;

        String[] date = data[2].split(" ");
        String[] year = date[0].split("/");

        yearAt = Integer.parseInt(year[2]);

        return yearAt;
    }

    // Function to get the "To" month data from the saved data
    public int getToMonthAt(int position){
        String data[] = getDataAt(position);
        int monthAt=0;

        String[] date = data[2].split(" ");
        String[] month = date[0].split("/");

        monthAt = Integer.parseInt(month[1]);

        return monthAt;
    }

    // Function to get the "To" day data from the saved data
    public int getToDayAt(int position){
        String data[] = getDataAt(position);
        int dayAt=0;

        String[] date = data[2].split(" ");
        String[] day = date[0].split("/");

        dayAt = Integer.parseInt(day[0]);

        return dayAt;
    }

    // Function to set the value of array list
    public void setArrayList(){
        String[] dataLine = contents.split("\n");
        alData = new ArrayList<String[]>();
        alData.clear();
        for(int i=0;i<dataLine.length;i++){
            String[] tempData = dataLine[i].split(";");
            // 0 = event name, 1 = start date time, 2 = end date time
            alData.add(tempData);
        }
    }

    // Function to get the arraylist
    public ArrayList<String[]> getArrayList(){
        setArrayList();
        return alData;
    }

    // Function for edit the data at certain location
    public boolean editDataAt(AlertDialog alrtDialog, FileOutputStream fos, int position, String evName, DatePicker dpF, TimePicker tpF, DatePicker dpT, TimePicker tpT){
        setArrayList();
        AlertDialog alertDialog = alrtDialog;
        alertDialog.setTitle("Alert");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        boolean flagEdit = false;
        try {
            FileOutputStream out = fos;

            String eventName = evName;
            DatePicker dpFrom = dpF;
            DatePicker dpTo = dpT;
            TimePicker tpFrom = tpF;
            TimePicker tpTo = tpT;

            tpFrom.clearFocus();
            tpTo.clearFocus();

            int fromHour = tpFrom.getCurrentHour();
            int fromMin = tpFrom.getCurrentMinute();
            int toHour = tpTo.getCurrentHour();
            int toMin = tpTo.getCurrentMinute();

            String fDate,tDate;
            fDate = String.valueOf(dpFrom.getDayOfMonth()) + "/" + String.valueOf(dpFrom.getMonth()+1) + "/" + String.valueOf(dpFrom.getYear()) + " " + Integer.toString(fromHour) + ":" + Integer.toString(fromMin);
            tDate = String.valueOf(dpTo.getDayOfMonth()) + "/" + String.valueOf(dpTo.getMonth()+1) + "/" + String.valueOf(dpTo.getYear()) + " " + Integer.toString(toHour) + ":" + Integer.toString(toMin);

            long fromDate,toDate;
            fromDate = getLong(fDate);
            toDate = getLong(tDate);

            flagEdit = validateDate(alertDialog, fromDate, toDate, fromHour, fromMin, toHour, toMin);

            if(flagEdit == true){
                boolean chkOverlap;
                chkOverlap = checkNotOverlap(fromDate,toDate,position);

                if(chkOverlap == true){
                    String[] tempData = alData.get(position);
                    tempData[0] = eventName;
                    tempData[1] = fDate;
                    tempData[2] = tDate;
                    alData.set(position, tempData);

                    String changedData = getAllStringToWriteFile(alData);
                    writeToFile(fos, changedData);
                    Toast.makeText(context,"Edited",Toast.LENGTH_SHORT).show();
                }
                else{
                    flagEdit = false;
                    String remainData = getAllStringToWriteFile(alData);
                    writeToFile(fos, remainData);
                    alertDialog.setMessage("Can't put multiple event in the same time");
                    alertDialog.show();
                }
            }
            else{
                String remainData = getAllStringToWriteFile(alData);
                writeToFile(fos, remainData);
            }
        }
        catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        return flagEdit;
    }

    public void deleteDataAt(FileOutputStream fos, int position) throws IOException {
        setArrayList();
        if(alData.size()>0) {
            alData.remove(position);
            String changedData = getAllStringToWriteFile(alData);

            writeToFile(fos, changedData);
            Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show();
        }
    }

    // Function to change the data in the array list into String format that will be written in the file
    public String getAllStringToWriteFile(ArrayList<String[]> arrayList){
        String currData = "";

        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.get(i).length; j++) {
                currData += arrayList.get(i)[j];
                if (j != arrayList.get(i).length - 1) {
                    currData += ";";
                }
            }
            currData += "\n";
        }

        return currData;
    }

    /*  Function to validate the new entry data
    *   Check if the input data is correct or not
    *   condition :
    *   - check if user put the event in past or not
    *   - check if the input for date and time is valid or not
    */
    public boolean validateDate(AlertDialog alrtDialog, long fDay, long tDay, int fH, int fM, int tH, int tM){
        boolean flag;
        AlertDialog alertDialog = alrtDialog;
        alertDialog.setTitle("Alert");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        Calendar c = Calendar.getInstance();

        if(fDay<c.getTimeInMillis()){
            flag = false;
            alertDialog.setMessage("Can't put event in the past");
            alertDialog.show();
        }
        else if(fDay < tDay){
            flag = true;
        }
        else if(fDay == tDay){
            if(fH < tH){
                flag = true;
            }
            else {
                if(fM <= tM){
                    flag = true;
                }
                else{
                    flag = false;
                    alertDialog.setMessage("Date Time invalid");
                    alertDialog.show();
                }
            }
        }
        else {
            flag = false;
            alertDialog.setMessage("Date Time Invalid");
            alertDialog.show();
        }
        return flag;
    }

    // Function to check if the new event data is overlapping or not with the current event data
    public boolean checkNotOverlap(long fromDate, long toDate, int editPos) throws ParseException, IOException {
        setArrayList();
        long startDate = fromDate;
        long endDate = toDate;
        boolean cond=true;

        if(alData.size()>1) {
            for (int i = 0; i < alData.size(); i++) {
                if(editPos == i){
                    continue;
                }
                long sDate, eDate;
                sDate = getLong(alData.get(i)[1]);
                eDate = getLong(alData.get(i)[2]);
                if(startDate == sDate && endDate == eDate){
                    cond = false;
                    break;
                }
                else if (startDate > eDate) {
                    cond = true;
                }
                else if (startDate < sDate) {
                    if (endDate <= sDate) {
                        cond = true;
                    } else {
                        cond = false;
                        break;
                    }
                }
                else {
                    cond = false;
                    break;
                }
            }
        }
        return cond;
    }

    // Convert string data with format "dd/MM/yyyy HH:mm" to long data
    public long getLong(String data) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d;
        d = sdf.parse(data);
        return d.getTime();
    }
}

