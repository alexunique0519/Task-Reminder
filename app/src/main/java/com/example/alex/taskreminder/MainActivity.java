package com.example.alex.taskreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.alex.taskreminder.Services.NotificationEventReceiver;
import com.example.alex.taskreminder.adapter.RecyclerViewAdapter;
import com.example.alex.taskreminder.model.TaskData;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickCallback {

    //define the RecyclerView object in this class
    private RecyclerView recView;
    //define the RecyclerView adapter in this class
    private RecyclerViewAdapter adapter;
    //define the ArrayList to store the data from database
    private ArrayList listData;
    //define the DBHelper object
    DBHelper db;

    //define all the constant string which can be the index value in bundle extras
    private static final String BUNDLE_EXTRA = "Bundle_Extra";
    private static final String ID = "ID";
    private static final String TASKNAME = "TaskName";
    private static final String DUE_DATE = "DATE";
    private static final String DUE_TIME = "TIME";
    private static final String DESCRIPTION = "DESCRIPTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //define the floatingActionButton object
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent intent = new Intent(this, DetailActivity.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when the fab button is clicked, go to DetailActivity
                startActivity(intent);
            }
        });

        //instantiate the db object
        db = new DBHelper(this);
        //get all the taskData when mainActivity creates
        listData = db.getAllTasks();


        //get the recyclerView instance
        recView = (RecyclerView) findViewById(R.id.task_list);
        //set layoutManage to recView
        recView.setLayoutManager(new LinearLayoutManager(this));

        //instantiate an recyclerViewAdapter object
        adapter = new RecyclerViewAdapter(listData, this);
        //attach the adapter to the recyclerView
        recView.setAdapter(adapter);
        //setItemClickCall functions for this adapter
        adapter.setItemClickCallback(this);

        //start the notification service using the android alermManager
        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }

    @Override
    protected void onResume(){
        super.onResume();

        //reload the data from database
        LoadDataFromDB();
        //pass new listData to the adapter
        adapter.setListData(listData);
        //every time when the adapter binded data changed, notifyDataChanged function should be called
        adapter.notifyDataSetChanged();
    }

    //define the loadDataFromDb function
    private void LoadDataFromDB(){
        listData = db.getAllTasks();
    }


    //override the onItemClick callback function defined in the recyclerViewAdapter
    //every time when user click one item in the recyclerView, the position of this item
    //will be passed to the onItemClick function
    @Override
    public void onItemClick(int pos) {
        //get the taskData from the passed pos valye
        TaskData td = (TaskData) listData.get(pos);
        //instantiate a intent object of DetailActivity
        Intent intent = new Intent(this, DetailActivity.class);
        //new bundle object and attach values to the object
        Bundle extras = new Bundle();
        extras.putInt(ID, td.taskId);
        extras.putString(TASKNAME, td.taskName);
        extras.putString(DUE_DATE, td.dueDate);
        extras.putString(DUE_TIME, td.dueTime);
        extras.putString(DESCRIPTION, td.description);
        //put the bundle onto the intent object
        intent.putExtras(extras);
        //start the detailActivity
        startActivity(intent);
    }

    //when the user click the complete button this function will be invoked,
    //first it will delete the current selected item in the database, and then listData will reload
    //the taskData from the database, set the data to adapter and notify the dataSet change.
    @Override
    public void onCompleteButtonClick(int pos) {
        TaskData td = (TaskData)listData.get(pos);
        db.deleteTask(td.taskId);

        listData = db.getAllTasks();
        adapter.setListData(listData);
        adapter.notifyDataSetChanged();
    }




}
