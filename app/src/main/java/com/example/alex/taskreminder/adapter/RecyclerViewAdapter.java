package com.example.alex.taskreminder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alex.taskreminder.R;
import com.example.alex.taskreminder.model.TaskData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Alex on 2016-06-18.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TaskListViewHolder> {

    //define the arrayList to store the data
    private ArrayList<TaskData> taskList;
    //define a layoutInflate object
    private LayoutInflater inflater;

    //define itemCLickCallback object
    private ItemClickCallback itemClickCallback;

    //in the constructor, assign the value to inflater and taskList
    public RecyclerViewAdapter(ArrayList listData, Context c){
        this.inflater = LayoutInflater.from(c);
        this.taskList = listData;
    }

    //define an interface for the ItemClickCallback function
    public interface ItemClickCallback{
        void onItemClick(int pos);
        void onCompleteButtonClick(int pos);
    }

    //define a function to set the callback function
    public void setItemClickCallback (final ItemClickCallback itemClickCallback){
        this.itemClickCallback = itemClickCallback;
    }

    //override the onCreateViewHolder function, the inflate will inflate the recyclerview with the
    //layout defined in the R.layout.List_item file
    @Override
    public TaskListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new TaskListViewHolder(view);
    }

    //Mapping the viewHolder element values to the taskList data according to position
    @Override
    public void onBindViewHolder(TaskListViewHolder holder, int position) {
        TaskData td = taskList.get(position);
        holder.taskName.setText(td.taskName);
        holder.DueDateTime.setText(td.dueDate + " " + td.dueTime);
    }

    //provide a function that the new data can be passed from adapter object
    public void setListData(ArrayList<TaskData> newList){
        this.taskList.clear();
        this.taskList.addAll(newList);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    //define the taskListViewHolder class and implement the onClickListener
    public class TaskListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //define all the user controls in one single item
        public TextView taskName;
        public TextView DueDateTime;
        public View container;
        public ImageButton ib;

        public TaskListViewHolder(View itemView){
            super(itemView);

            //bind the user control object with the element defined in the resource file
            taskName = (TextView)itemView.findViewById(R.id.task_name);
            DueDateTime = (TextView)itemView.findViewById(R.id.task_dueTime);
            ib = (ImageButton)itemView.findViewById(R.id.complete_button);
            ib.setOnClickListener(this);
            container = itemView.findViewById(R.id.content_item);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //will be invoked when the item being clicked
            if(v.getId() == R.id.content_item){
                itemClickCallback.onItemClick(getAdapterPosition());
            }
            //will be invoked when the complete button being clicked
            else{
                itemClickCallback.onCompleteButtonClick(getAdapterPosition());
            }
        }
    }

}
