package com.ditkevinstreet.todomanager;

/**
 * Created by Admin on 19/11/2017.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;


import com.ditkevinstreet.todomanager.ToDoItem.Status;


public class ToDoListAdapter extends BaseAdapter{

    // List of ToDoItems
    private final List<ToDoItem> mItems = new ArrayList<ToDoItem>();

    final Context mContext;
    int cyan1;
    int magenta1;
    Date todaysDate;
    Date deadline;

    private static final String TAG = "Lab-UserInterface";

    public ToDoListAdapter(Context context) {

        mContext = context;
        cyan1 = mContext.getColor(R.color.CYAN1);
        magenta1 = mContext.getColor(R.color.MAGENTA1);

    }

    // Add a ToDoItem to the adapter
    // Notify observers that the data set has changed

    public void add(ToDoItem item) {

        mItems.add(item);
        notifyDataSetChanged();

    }

    public void remove(int position) {

        mItems.remove(position);
        notifyDataSetChanged();

    }

    // Clears the list adapter of all items.

    public void clear(){

        mItems.clear();
        notifyDataSetChanged();

    }

    // Returns the number of ToDoItems

    @Override
    public int getCount() {

        return mItems.size();

    }

    // Retrieve the number of ToDoItems

    @Override
    public Object getItem(int pos) {

        return mItems.get(pos);

    }
    //return the toDoItem at the given position
    public ToDoItem getToDoItem(int position){
        return mItems.get(position);
    }

    // Get the ID for the ToDoItem
    // In this case it's just the position

    @Override
    public long getItemId(int pos) {

        return pos;

    }

    //TODO Additional Functionalirty 5 - Methods to sort the listview by various parameters
    //list sort by title
    public void sortAlphabetically(){
        Collections.sort(mItems, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem t1, ToDoItem t2) {
                return t1.getTitle().compareTo(t2.getTitle());
                //return 0;
            }
        });
    }

    //list sort by priority
    public void sortPriority(){
        Collections.sort(mItems, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem t1, ToDoItem t2) {
                return t1.getPriority().compareTo(t2.getPriority());
                //return 0;
            }
        });
    }
    //list sort by deadline
    public void sortDate(){
        Collections.sort(mItems, new Comparator<ToDoItem>() {
            @Override
            public int compare(ToDoItem t1, ToDoItem t2) {
                return t1.getDate().compareTo(t2.getDate());
                //return 0;
            }
        });
    }

    //Create a View to display the ToDoItem
    // at specified position in mItems

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        //TODO - Get the current ToDoItem - done
        final ToDoItem toDoItem = (ToDoItem) getItem(position);

        //TODO - Inflate the View for this ToDoItem - done
        // from todo_item.xml.
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemLayout = inflater.inflate(R.layout.todo_item, parent, false);


        //TODO - Fill in specific ToDoItem data - done
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file

        //TODO - Display Title in TextView - done
        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        titleView.setText(toDoItem.getTitle());

       //TODO Additional functionality 2.1 - Change title color red 1 day from deadline
        todaysDate = new Date();
        long todayLong = todaysDate.getTime();
        deadline = toDoItem.getDate();
        long deadlineLong = deadline.getTime();

        if((deadlineLong - todayLong)< 86400000 ){
            titleView.setTextColor(Color.RED);
        }


        // TODO - Set up Status CheckBox-done
        final CheckBox statusView = (CheckBox) itemLayout.findViewById(R.id.statusCheckBox);
        statusView.setChecked(toDoItem.getStatus() == Status.DONE);

        statusView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                log("logMessage : Entered onCheckedChanged()");

                // TODO - Set up and implement an OnCheckedChangeListener, which - done
                // is called when the user toggles the status checkbox
                //TODO Additional Functionality 1.2 - Changing background color based on status change
                if(isChecked){
                    toDoItem.setStatus(Status.DONE);
                    itemLayout.setBackgroundColor(cyan1);

                }else{
                    toDoItem.setStatus(Status.NOTDONE);
                    itemLayout.setBackgroundColor(magenta1);
                }
            }
        });

        //TODO - Display Priority in a TextView - done

        //The old priority View TextView
        /*final TextView priorityView = (TextView) itemLayout.findViewById(R.id.priorityView);
        priorityView.setText(toDoItem.getPriority().toString());
        */

        //TODO Additional Functionality 3 - Priority spinner
        final Spinner priorityViewSpin = (Spinner) itemLayout.findViewById(R.id.priorityViewSpin);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,//this may cause issues
                R.array.PriorityChoices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priorityViewSpin.setAdapter(adapter);

        //setting initial priority value
        final String priorityString = toDoItem.getPriority().toString();
        if(priorityString.equals("LOW")){
            priorityViewSpin.setSelection(0);
            toDoItem.setPriority(ToDoItem.Priority.LOW);
        }
        else if(priorityString.equals("MED")){
            priorityViewSpin.setSelection(1);
            toDoItem.setPriority(ToDoItem.Priority.MED);
        }
        else if(priorityString.equals("HIGH")){
            priorityViewSpin.setSelection(2);
            toDoItem.setPriority(ToDoItem.Priority.HIGH);
        }
        else{
            Log.d(TAG, "logMessage : Invalid priority value");
        }
        Log.d(TAG, "logMessage : priority is : " + priorityString   );

        //changing the priority value
        priorityViewSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)

                //im not sure if more is required here, do i need to update the priority in the object

                String item = adapterView.getItemAtPosition(position).toString();
                priorityViewSpin.setSelection(position);

                //
                if(position == 0){
                    toDoItem.setPriority(ToDoItem.Priority.LOW);
                    Log.d(TAG, "logMessage : priority changed to low");
                }
                else if(position == 1){
                    toDoItem.setPriority(ToDoItem.Priority.MED);
                    Log.d(TAG, "logMessage : priority changed to medium");
                }
                else if(position == 2){
                    toDoItem.setPriority(ToDoItem.Priority.HIGH);
                    Log.d(TAG, "logMessage : priority changed to high");
                }
                else{
                    Log.d(TAG, "logMessage : invalid priority selected");
                }
                sortPriority();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Another interface callback

            }
        });


        // TODO - Display Time and Date. - done
        // Hint - use ToDoItem.FORMAT.format(toDoItem.getDate()) to get date and time String
        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        //dateView.setText(toDoItem.getDate().toString());
        dateView.setText(ToDoItem.FORMAT.format(toDoItem.getDate()));

        //TODO Additional Functionality 1.1 - Setting background color based on status
        //sets initial color, above controls color change
        if(statusView.isChecked()){
            itemLayout.setBackgroundColor(cyan1);}
        else{
            itemLayout.setBackgroundColor(magenta1);
        }

        // Return the View you just created
        return itemLayout;

    }

    private void log(String msg) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, msg);
    }
}
