package com.example.alex.taskreminder.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.alex.taskreminder.DBHelper;
import com.example.alex.taskreminder.NotificationActivity;
import com.example.alex.taskreminder.R;
import com.example.alex.taskreminder.model.TaskData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Alex on 2016-06-21.
 */
//define the notificationIntentService by extending the intentService
public class NotificationIntentService extends IntentService {
    //define the constant value for notificationId, action start and action delete
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    String currentTaskName = "";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    //define the createIntentStartNotificationService function, instantiate a NotificationIntentService intent, set action to it
    //and return the intent
    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    //define the createIntentDeleteNotification function, instantiate a NotificationIntentService intent, set action to it
    //and return the intent
    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    //override the onHandleIntent function,
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            //first get the action from the passed intent
            String action = intent.getAction();
            //if the action is start action, then check the latest uncompleted task, if both of them
            //are ture, then process the start notification function
            if (ACTION_START.equals(action) && CheckLateTask()) {

                processStartNotification(currentTaskName);
            }
        } finally {
            //Finish the execution from a previous {@link #startWakefulService}.  Any wake lock
            // that was being held will now be released.
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {

    }

    //define the processStartNotification function, and pass the task name which will be displayed in the notification
    private void processStartNotification(String TaskName) {
        //define the notification builder
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        //the notification builder title will be "You have task to complete", set icon, color and type and sound in this case
        builder.setContentTitle("You have task to complete")
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(TaskName + " is not completed on time")
                .setSmallIcon(R.drawable.ic_dialog_info)
                .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true);
        //instantiate intent with the NotificationActivity class
        Intent mainIntent = new Intent(this, NotificationActivity.class);
        //instantiate a pendingIntent with the mainIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //set the contentintent with the pendingIntent we defined above
        builder.setContentIntent(pendingIntent);
        //set the deleteIntent
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));
        //instantiate a notification manager
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        //notify the notification by calling the notify function of the manager object
        manager.notify(NOTIFICATION_ID, builder.build());
    }
    //this function is used to che the earliest task that not completed in the task list
    private boolean CheckLateTask(){
        //instantiate a dbhelper object, because we need to get the data from database
        DBHelper db = new DBHelper(this);
        //store the data in  arrayList
        ArrayList<TaskData> listData = db.getAllTasks();
        if(listData.size() == 0)
        {
            return  false;
        }
        //sort the arrayList using the comparator
        Collections.sort(listData, new TaskDataComparator());
        //after sorting the arrayList, the first one is the earliest task we need to complete
        TaskData td = listData.get(0);
        Date date = null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
             date = df.parse(td.dueDate + " " + td.dueTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //get the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //if the current time value is greater or equals than the dateTime value in the task, that means
        //the task is not complete on time, we record the task name and send notification
        if(calendar.getTimeInMillis() >= date.getTime() )
        {

            currentTaskName = td.taskName;
            return true;
        }

        return false;
    }
    //define the comparator by inplements the Comparator interface
    public class TaskDataComparator implements Comparator<TaskData>
    {

        @Override
        public int compare(TaskData lhs, TaskData rhs) {
            //define the dateformat we use to parse the dateTime value
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateLeft = null;
            Date dateRight = null;
            try {
                //parse the first value
                dateLeft = df.parse(lhs.dueDate + " " +lhs.dueTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                //parse the second value
                dateRight = df.parse(rhs.dueDate + " " +rhs.dueTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //compare and return the result
            if(dateLeft.getTime() - dateRight.getTime() >= 0)
            {
                return 1;
            }
            else{
                return -1;
            }

        }
    }

}