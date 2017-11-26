package com.ditkevinstreet.todomanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ToDoManagerActivity extends AppCompatActivity {

    private static final String TAG = "Lab-UserInterface";

    // Add a ToDoItem Request Code
    private static final int ADD_TODO_ITEM_REQUEST = 0;

    private static final String FILE_NAME = "TodoManagerActivityData.txt";
    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;
    private static final int MENU_DUMP = Menu.FIRST + 1;

    Button button;
    private FloatingActionButton fab;
    private FloatingActionButton fabStopAlarm;


    ListView listView;
    //ArrayList<ToDoItem> arrayList;
    //ArrayAdapter<ToDoItem> arrayAdaptor; //experiment
    ToDoItem toDoItem;
    public ToDoListAdapter mAdapter;
    //CoordinatorLayout coordinatorLayout1;
    //GridLayout toDoGrid;
    View toDoItemLinearLayout;
    //TextView title;

    AlarmManager alarmManager;
    int alarmTicker;
    Context mContext;
    Calendar calendar;
    Intent alarmIntent;
    PendingIntent pendingIntentSet;
    PendingIntent pendingIntentCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "logMessage : onCreate: Entered on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the listView
        listView = (ListView) findViewById(R.id.toDoListView);

        alarmTicker = 0;

       //is this needed?
        View layout = getLayoutInflater().inflate(R.layout.todo_item, null);
        toDoItemLinearLayout = (LinearLayout) layout.findViewById(R.id.toDoItemLinearLayout);

        // Create a new TodoListAdapter for this ListActivity's ListView
        mAdapter = new ToDoListAdapter(getApplicationContext());

        this.mContext = this;

        //initialize alarm picker
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("Entered fab.OnClickListener.onClick()");
                Intent intent = new Intent(getApplicationContext(), AddToDoActivity.class);
                startActivityForResult(intent, ADD_TODO_ITEM_REQUEST);
            }
        });

        fabStopAlarm = (FloatingActionButton) findViewById(R.id.fabStopAlarm);
        fabStopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log("Entered fabStopAlarm.OnClickListener.onClick()");

                //cancels the alarm
                //alarmManager.cancel(pendingIntent);

                //put extra string into alarmIntent to tell the clock we pressed silence alarm button
                alarmIntent.putExtra("extra", "alarm off");


                //stops ringtone
                sendBroadcast(alarmIntent);


            }
        });


        //TODO - Attach the adapter to this ListActivity's ListView - done
        listView.setAdapter(mAdapter);

        //intent for alarm receiver class
        alarmIntent = new Intent(this.mContext, AlarmReceiver.class);


        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Toast.makeText(ToDoManagerActivity.this, "You long pressed in main", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "logMessage : long clicked an item in main");
                Vibrator vibrator = (Vibrator) ToDoManagerActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);

                AlertDialog.Builder builder = new AlertDialog.Builder(ToDoManagerActivity.this);
                builder.setMessage("Do you want to delete this to do item?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "logMessage : clicked yes to delete item ");
                                ToDoItem toBeRemoved = mAdapter.getToDoItem(position);
                                Date timeOfAlarmToBeRemoved = toBeRemoved.getAlarmTime();
                                mAdapter.remove(position);

                                pendingIntentCancel = PendingIntent.getBroadcast(ToDoManagerActivity.this, toBeRemoved.getAlarmRequestCode(), alarmIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                                //cancel alarm manager
                                alarmManager.cancel(pendingIntentCancel);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                Log.d(TAG, "logMessage : clicked no to not delete item ");
                            }
                });
                AlertDialog alert = builder.create();
                alert.setTitle("Alert");
                alert.show();

                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("Entered onActivityResult()");
        if (requestCode == ADD_TODO_ITEM_REQUEST) {
            if(resultCode == RESULT_OK) {
                toDoItem = new ToDoItem(data);
                mAdapter.add(toDoItem);
                //mAdapter.sortAlphabetically();
                mAdapter.sortPriority();
                //mAdapter.sortDate();

                //TODO Additional Functionality 2.2  - Alarm

                //setting alarm time
                Date alarmTime = toDoItem.getAlarmTime();
                calendar.setTime(alarmTime);

                //put in extra string into alarmIntent, telling clock whether alarm is being set or silenced
                alarmIntent.putExtra("extra", "alarm on");


                //create pending intent to delay intent until alarm time
                toDoItem.setAlarmRequestCode(alarmTicker);
                pendingIntentSet = PendingIntent.getBroadcast(ToDoManagerActivity.this, alarmTicker, alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                alarmTicker++;

                //set alarm manager
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentSet);




            }
        }

    }

    // Do not modify below here

    @Override
    public void onResume() {
        super.onResume();

        // Load saved ToDoItems, if necessary

        if (mAdapter.getCount() == 0)
            loadItems();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Save ToDoItems

        saveItems();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
        menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                mAdapter.clear();
                return true;
            case MENU_DUMP:
                dump();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dump() {

        for (int i = 0; i < mAdapter.getCount(); i++) {
            String data = ((ToDoItem) mAdapter.getItem(i)).toLog();
            log("Item " + i + ": " + data.replace(ToDoItem.ITEM_SEP, ","));
        }

    }

    // Load stored ToDoItems
    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String title = null;
            String priority = null;
            String status = null;
            Date date = null;

            while (null != (title = reader.readLine())) {
                priority = reader.readLine();
                status = reader.readLine();
                date = ToDoItem.FORMAT.parse(reader.readLine());
                mAdapter.add(new ToDoItem(title, ToDoItem.Priority.valueOf(priority),
                        ToDoItem.Status.valueOf(status), date));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Save ToDoItems to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < mAdapter.getCount(); idx++) {

                writer.println(mAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
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