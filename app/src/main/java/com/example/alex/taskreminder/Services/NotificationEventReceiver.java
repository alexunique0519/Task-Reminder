package com.example.alex.taskreminder.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alex on 2016-06-21.
 */
//Defined a notificationEventReceiver class by extends the WakefulBroadcastReceiver
public class NotificationEventReceiver extends WakefulBroadcastReceiver {
    //define constant string for passing the action
    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    //define the number of hours for monistoring and alerting user
    private static final int NOTIFICATIONS_INTERVAL_IN_HOURS = 4;

    //define the function for setting up the alerm, in this case we will user alermManager to
    //check the taskList and broadcast the pendingIntend every four hours
    public static void setupAlarm(Context context) {
        //get alarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //assign the alarmIntent by getStartpendingIntent() function
        PendingIntent alarmIntent = getStartPendingIntent(context);
        //set the repeating from now on, the alarm type is RTC_WAKEUP, the interval is four hours, and the intent is the
        //alarmIntent we defined above
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTriggerAt(new Date()),
                NOTIFICATIONS_INTERVAL_IN_HOURS * AlarmManager.INTERVAL_HOUR,
                //NOTIFICATIONS_INTERVAL_IN_HOURS * 60 * 1000,
                alarmIntent);
    }

    //this function is for canceling the alarm
    public static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.cancel(alarmIntent);
    }

    //pass the date, will return the value in milliseconds
    private static long getTriggerAt(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        //calendar.add(Calendar.HOUR, NOTIFICATIONS_INTERVAL_IN_HOURS);
        return calendar.getTimeInMillis();
    }

    //this function will instantiate an intent, attach the start notification action to it, and perform a broadcasting
    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //this function will instantiate an intent, attach the delete notification action to it, and perform a broadcasting
    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //this function will be triggered when receiving the broadcasting intent.
    @Override
    public void onReceive(Context context, Intent intent) {
        //get the action from the intent
        String action = intent.getAction();
        Intent serviceIntent = null;
        //if the action equals ACTION_START_NOTIFICATION_SERVICE, create a start notification service
        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive from alarm, starting notification service");
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
        }
        //if the action equals ACTION_DELETE_NOTIFICATION, create a delete notification service
        else if (ACTION_DELETE_NOTIFICATION.equals(action)) {
            Log.i(getClass().getSimpleName(), "onReceive delete notification action, starting notification service to handle delete");
            serviceIntent = NotificationIntentService.createIntentDeleteNotification(context);
        }

        if (serviceIntent != null) {
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, serviceIntent);
        }
    }
}
