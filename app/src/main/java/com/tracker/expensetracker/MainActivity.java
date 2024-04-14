package com.tracker.expensetracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.tracker.expensetracker.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    CheckBox useCrrDT;
    EditText exp_title,exp_amount;
    Button exp_time,addExp;
    SimpleDateFormat dateFormat,dbDate;
    DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        useCrrDT = findViewById(R.id.useCrrDT);
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        dbDate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        dbhelper = new DatabaseHelper(this);



        setSupportActionBar(binding.appBarMain.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setView(R.layout.add_expense);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                exp_title = alertDialog.findViewById(R.id.exp_title);
                exp_amount = alertDialog.findViewById(R.id.exp_amount);
                useCrrDT = alertDialog.findViewById(R.id.useCrrDT);
                addExp = alertDialog.findViewById(R.id.addExp);
                exp_time = alertDialog.findViewById(R.id.exp_time);

                if(useCrrDT.isChecked()){
                    exp_time.setText(dateFormat.format(Calendar.getInstance().getTime()));
                    exp_time.setEnabled(false);
                }
                useCrrDT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(useCrrDT.isChecked()){
                            exp_time.setText(dateFormat.format(Calendar.getInstance().getTime()));
                            exp_time.setEnabled(false);
                        }
                        if(!useCrrDT.isChecked()){
                            exp_time.setText("");
                            exp_time.setEnabled(true);
                        }
                    }
                });

                exp_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickDateTime();
                    }
                });

                addExp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addExpense(alertDialog);
                    }
                });
            }
        });
    }

    public void pickDateTime() {
//        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        final Calendar c = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);

//                        SimpleDateFormat date = new SimpleDateFormat("dd-MM-YY hh:mm a");
                        Date dt = c.getTime();
                        exp_time.setText(dateFormat.format(dt));
                    }
                };
                new TimePickerDialog(MainActivity.this,timeListener,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false).show();
            }
        };
        new DatePickerDialog(MainActivity.this,dateListener,c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void addExpense(AlertDialog alertDialog){
        int okay = 1;
        String title = exp_title.getText().toString();
        String amount = exp_amount.getText().toString();
        String date = exp_time.getText().toString();
        if (title.trim().isEmpty()){
            exp_title.setError("*Required");
            okay = 0;
        }
        if(amount.trim().isEmpty()){
            exp_amount.setError("*Required");
            okay = 0;
        }
        if(date.isEmpty()){
            exp_time.setError("*Required");
            okay = 0;
        }
        if (okay==1){
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Title",title);
            cv.put("Amount",String.format((amount),"%.2f"));
            try {
                Date dt = dateFormat.parse(date);
                String dateF = dbDate.format(dt);
                cv.put("Date",dateF);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            db.insert("spends",null,cv);
            alertDialog.dismiss();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
//            SQLiteDatabase db = dbhelper.getWritableDatabase();
//            db.execSQL("DELETE FROM SPENDS");
            return true;
        }
        if (id == R.id.graph){
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.graphF);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


}