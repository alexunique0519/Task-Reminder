package com.example.alex.taskreminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.app.DatePickerDialog.*;

public class DetailActivity extends AppCompatActivity {

    //define a bunch of constant string as the index in the bundle extras
    private static final String BUNDLE_EXTRA = "Bundle_Extra";
    private static final String ID = "ID";
    private static final String TASKNAME = "TaskName";
    private static final String DUE_DATE = "DATE";
    private static final String DUE_TIME = "TIME";
    private static final String DESCRIPTION = "DESCRIPTION";

    //define a dbhelper object
    private DBHelper db;

    //define all the editText variables
    EditText etId;
    EditText etTaskName;
    EditText etDueDate;
    EditText etDueTime;
    EditText etDescription;
    Button btnDatePicker;
    Button btnTimePicker;
    int Id;
    //define the constant variables for operations
    final int ADD_OPERATION = 1;
    final int EDIT_OPERATION = 2;
    //define the value to get date and time value
    private int Year, Month, Day, Hour, Minute;
    //use this value to record the current type of operation
    private int m_operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //if the getSupportActionBar()not equals null then provide the navigation up functionality
        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //instantiate a DBHelper object
        db = new DBHelper(this);

        //get all the editText
        etId = (EditText)findViewById(R.id.task_id);
        etTaskName = (EditText)findViewById(R.id.task_name);
        etDueDate = (EditText)findViewById(R.id.due_date);
        etDueTime = (EditText)findViewById(R.id.due_time);
        etDescription = (EditText)findViewById(R.id.description);
        btnDatePicker=(Button)findViewById(R.id.date_button);
        btnTimePicker=(Button)findViewById(R.id.time_button);
        //subscribe the datePicker button onClick event
        subscribeButtonDatePickerOnClickEvent();
        //subscribe the timePicker button onClick event
        subscribeButtonTimePickerOnClickEvent();

        //get the value of bundle
        Intent intentExtras = getIntent();
        Bundle extraBundle = intentExtras.getExtras();

        //if the bundle value is null or empty that means the current operation should be add
        if(extraBundle == null || extraBundle.isEmpty()){
            //if adding a new record, hide the id EditText
            //etId.setVisibility(View.GONE);
            etTaskName.requestFocus();
            m_operation = ADD_OPERATION;
        }
        //otherwise means we are in a edit operation
        else{
            m_operation = EDIT_OPERATION;

            //means this is the interface for editing the mark info
            Id = extraBundle.getInt(ID);
            String taskName = extraBundle.getString(TASKNAME);
            String dueDate = extraBundle.getString(DUE_DATE);
            String dueTime = extraBundle.getString(DUE_TIME);
            String description = extraBundle.getString(DESCRIPTION);

            //set the text to all the textEdit
            etId.setText(String.valueOf(Id));
            etTaskName.requestFocus();
            etTaskName.setText(taskName);
            etDueDate.setText(dueDate);
            etDueTime.setText(dueTime);
            etDescription.setText(description);
        }

    }

    //define the subscribeButtonDatePickerOnClickEvent function so that when user finish picking a date,
    //the dateString will be set on the textEdit
    private void subscribeButtonDatePickerOnClickEvent(){
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                Year = c.get(Calendar.YEAR);
                Month = c.get(Calendar.MONTH);
                Day = c.get(Calendar.DAY_OF_MONTH);

                //Create the date picker dialog
                DatePickerDialog dialog = new DatePickerDialog(DetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etDueDate.setText(year + "-" + (monthOfYear + 1 > 9 ? monthOfYear + 1 : "0" + (monthOfYear + 1)) + "-" + dayOfMonth);
                    }
                }, Year, Month, Day);

                dialog.show();
            }
        });
    }

    //define the subscribeButtonTimePickerOnClickEvent function so that when user finish picking a time,
    //the dateString will be set on the textEdit
    private void subscribeButtonTimePickerOnClickEvent(){
        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                Hour = c.get(Calendar.HOUR_OF_DAY);
                Minute = c.get(Calendar.MINUTE);

                // Create Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(DetailActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                etDueTime.setText((hourOfDay > 9 ? hourOfDay : "0" + hourOfDay )+ ":" + (minute > 9 ? minute : "0" + minute));
                            }
                        }, Hour, Minute, false);
                timePickerDialog.show();
            }
        });
    }

    //override the onPrepareOptionsMenu function so that the "SAVE" button is visible directly instead of showing three dots
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.Add);
        item.setVisible(true);
        super.onPrepareOptionsMenu(menu);
        return true;
    }


    //create the option menu, which is the "SAVE" button in the detail activity
    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //override the onOptionItemSelected function, so that when user clicks this button, store the data into database
    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        // when user click the option menu, the selected item will be passed in, in the current case there is only one button
        if(item.getItemId() == R.id.Add){
            //get taskName string from editText
            String taskName = etTaskName.getText().toString().trim();
            //if the taskName is empty, will show a dialog to ask user to input valid value
            if(taskName.isEmpty()){
                createAlertDialog("Please input valid task name.");
                return false;
            }
            String dueDate = etDueDate.getText().toString().trim();
            if(dueDate.isEmpty()){
                createAlertDialog("Please select the due date of this task");
                return false;
            }
            String dueTime = etDueTime.getText().toString().trim();
            if(dueDate.isEmpty()){
                createAlertDialog("Please select the due time of this task");
                return false;
            }
            //the description is not a required field, so if it is empty the value is still valid
            String description = etDescription.getText().toString();

            //if the operation mark is ADD_OPERATION, then insert the record to the database
            if(m_operation == ADD_OPERATION)
            {
                if (db.insertRecord(taskName, dueDate, dueTime, description)) {
                    this.finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Task details failed to save", Toast.LENGTH_SHORT).show();
                }
            }
            //if the operation mark is EDIT_OPERATION, then update the record in database
            else if(m_operation == EDIT_OPERATION){
                if(db.updateTaskInfo(Id, taskName, dueDate, dueTime, description)){
                    this.finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Task details failed to update", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //if user click the back arrow, go up to the mainActivity
        else  if(item.getItemId() == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return  true;
    }

    //define a function to create AlertDialog which will display the message if user input is not valid
    private void createAlertDialog(String errorMessage){
        //instantiate an AlertDialog builder
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(DetailActivity.this);

        //set the title and message when showing the dialog
        adBuilder.setMessage(errorMessage)
                .setTitle("Warning");

        //set the positive button text and click listener
        adBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               return;
            }
        });

        // create the alert dialog
        AlertDialog dialog = adBuilder.create();
        // show the dialog
        dialog.show();
    }

}
