package com.example.alex.taskreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.alex.taskreminder.model.TaskData;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Alex Yan on 2016-06-19.
 */
public class DBHelper extends SQLiteOpenHelper {

    //define constant strings for the database column name
    private static final String DB_NAME = "TaskReminder.db";
    private static final String TABLE_NAME = "tasks";
    private static final String ID_COLUMN = "id";
    private static final String TASKNAME_COLUMN = "taskName";
    private static final String DUEDATE_COLUMN = "dueDate";
    private static final String DUETIME_COLUMN = "dueTime";
    private static final String DESCRIPTION_COLUMN = "description";

    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //execute the sql query to generate tasks table
        db.execSQL(
                "create table tasks (id integer primary key autoincrement, taskName text, dueDate text, dueTime text, description text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if there is new version, drop the table and create a new one
        db.execSQL("DROP TABLE IF EXISTS marks");
        onCreate(db);
    }

    //define the function to insert record
    public boolean insertRecord(String taskName, Date dueDate, Time dueTime, String description){
        //get a writableDatebase object before inserting
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        //put all the values to the corresponding columns
        ContentValues cv = new ContentValues();
        cv.put(TASKNAME_COLUMN, taskName);
        //using simpledateFormat to convert the dueDate to a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cv.put(DUEDATE_COLUMN, dateFormat.format(dueDate));
        //using simpledateFormat to convert the dueTime to be a string
        dateFormat = new SimpleDateFormat("HH:mm");
        cv.put(DUETIME_COLUMN, dateFormat.format(dueTime));
        cv.put(DESCRIPTION_COLUMN, description);

        //insert the data to database
        long rowId = db.insert(TABLE_NAME, null, cv);

        if(rowId != 0){
            db.setTransactionSuccessful();
            db.endTransaction();

            return  true;
        }

        return  false;
    }

    public boolean insertRecord(String taskName, String dueDate, String dueTime, String description){
        //get a writableDatebase object before inserting
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        //put all the values to the corresponding columns
        ContentValues cv = new ContentValues();
        cv.put(TASKNAME_COLUMN, taskName);
        cv.put(DUEDATE_COLUMN, dueDate);
        cv.put(DUETIME_COLUMN, dueTime);
        cv.put(DESCRIPTION_COLUMN, description);

        //insert the data to database
        long rowId = db.insert(TABLE_NAME, null, cv);

        if(rowId != 0){
            db.setTransactionSuccessful();
            db.endTransaction();

            return  true;
        }

        return  false;
    }

    //get the cursor by a specific record id
    public Cursor getData(int id){
        //get a readable database object to retrieve data from database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from tasks where id = " + id, null);
        return  res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    //update the task info
    public  boolean updateTaskInfo(int id, String taskName, Date dueDate, Time dueTime, String description){
        //get the writableDatabase object before updating
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASKNAME_COLUMN, taskName);
        //using simpledateFormat to convert the dueDate to a string
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cv.put(DUEDATE_COLUMN, dateFormat.format(dueDate));
        //using simpledateFormat to convert the dueTime to be a string
        dateFormat = new SimpleDateFormat("HH:mm");
        cv.put(DUETIME_COLUMN, dateFormat.format(dueTime));
        cv.put(DESCRIPTION_COLUMN, description);
        //return the number of rows that have been affected by updating the database
        int rows =  db.update(TABLE_NAME, cv, "id = ?", new String[]{Integer.toString(id)});
        if(rows > 0)
        {
            return  true;
        }
        return  false;
    }

    //update the task info
    public  boolean updateTaskInfo(int id, String taskName, String dueDate, String dueTime, String description){
        //get the writableDatabase object before updating
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASKNAME_COLUMN, taskName);
        cv.put(DUEDATE_COLUMN, dueDate);
        cv.put(DUETIME_COLUMN, dueTime);
        cv.put(DESCRIPTION_COLUMN, description);
        int rows =  db.update(TABLE_NAME, cv, "id = ?", new String[]{Integer.toString(id)});
        if(rows > 0)
        {
            return  true;
        }
        return  false;
    }

    //delete the task by id
    public boolean deleteTask(int id){
        //get the writableDatabase object before deleting a record
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_NAME, "id = ?", new String[]{Integer.toString(id)});
        if(rows > 0){
            return true;
        }
        return  false;
    }

    //define a function to return an arrayList of all the task info
    public ArrayList<TaskData> getAllTasks(){
        //new an arrayList to store the data
        ArrayList<TaskData> taskList = new ArrayList<TaskData>();
        //get a readable database object
        SQLiteDatabase db = this.getReadableDatabase();
        //run a query to get all the data from the table
        Cursor res = db.rawQuery("SELECT * FROM tasks", null);
        //move the cursor to the first position
        res.moveToFirst();

        //define a while loop that if the cursor is valid, get the value by corresponding cursor and
        //assign the value to taskData object one by one
        while(res.isAfterLast() == false){
            TaskData td = new TaskData();
            td.taskId = res.getInt(res.getColumnIndex(ID_COLUMN));
            td.taskName = res.getString(res.getColumnIndex(TASKNAME_COLUMN));
            td.dueDate = res.getString(res.getColumnIndex(DUEDATE_COLUMN));
            td.dueTime = res.getString(res.getColumnIndex(DUETIME_COLUMN));
            td.description = res.getString(res.getColumnIndex(DESCRIPTION_COLUMN));

            taskList.add(td);
            res.moveToNext();
        }

        return taskList;
    }

}
