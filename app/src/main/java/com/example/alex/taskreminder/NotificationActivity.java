package com.example.alex.taskreminder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;

//define the notificationActivity to display the message
public class NotificationActivity extends AppCompatActivity {

    //define a insult message array
    String[] NotificationMessages = {"You are late getting this task done. Your mother would be disappointed by you.",
        "Don't be so lazy, buddy. You have to finish your task.", "Don't push everything to tomorrow."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //get the random index
        Random rand = new Random();
        int randIndex = rand.nextInt(3);

        //set the random message onto the activity
        TextView tv = (TextView)findViewById(R.id.message);
        tv.setText(NotificationMessages[randIndex]);
    }
}
