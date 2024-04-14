package com.tracker.expensetracker;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.tracker.expensetracker.databinding.HomeBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Date;

public class Home extends Fragment {

    private HomeBinding binding;
    DatabaseHelper dbHelper;
    SimpleDateFormat dateFormat,dbDate;
    TableLayout Expenses;
    Button viewTitle,viewAmount,viewDate;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = HomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getContext());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        dbDate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Expenses = binding.Expenses;
        viewTitle = binding.viewTitle;
        viewAmount = binding.viewAmount;
        viewDate = binding.viewDate;

        getExpense("Date");
        viewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpense("Title");
                viewTitle.setText("Title ▼");
                viewAmount.setText("Amount");
                viewDate.setText("Date");

//                button.setText("Button");
            }
        });
        viewAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpense("Amount");
                viewTitle.setText("Title");
                viewAmount.setText("Amount ▼");
                viewDate.setText("Date");
            }
        });
        viewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpense("Date");
                viewTitle.setText("Title");
                viewAmount.setText("Amount");
                viewDate.setText("Date ▼");
            }
        });
    }


    @SuppressLint({"Range","Recycle"})
    public void getExpense(String param){
        Expenses.removeAllViews();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columns = {"Title","Amount","Date"};
        Cursor c = db.query("spends",columns,null,null,param,null,param);
        List<List<String>> data = new ArrayList();
        while (c.moveToNext()){
            String title = c.getString(c.getColumnIndex("Title"));
            String amount = c.getString(c.getColumnIndex("Amount"));
            String date = c.getString(c.getColumnIndex("Date"));
            List<String> ent = new ArrayList<>();
            ent.add(title);ent.add(amount);ent.add(date);
            data.add(ent);
        }
        if(param=="Date"){
            Collections.reverse(data);
        }

        for (List<String> item: data) {
            String Title = item.get(0);
            String Amount = item.get(1);
            String Date = item.get(2);
            rowGenerator(Title,Amount,Date);
        }
    }

    public void rowGenerator(String Title,String Amount, String DateText){
        TableRow tr = new TableRow(getContext());

        TextView title = new TextView(getContext());
        title.setWidth((int) getResources().getDimension(R.dimen.TITLE_WIDTH));
        title.setGravity(Gravity.CENTER);
        title.setPadding(10,10,10,10);
        title.setTextSize(20);

        TextView amount = new TextView(getContext());
        amount.setWidth((int) getResources().getDimension(R.dimen.AMOUNT_WIDTH));
        amount.setGravity(Gravity.CENTER);
        amount.setPadding(10,10,10,10);
        amount.setTextSize(20);

        TextView date = new TextView(getContext());
        date.setGravity(Gravity.CENTER);
        date.setPadding(10,10,10,10);
        date.setTextSize(20);

        title.setText(Title);
        amount.setText(Amount);
        String dateFinal = null;

        try {
            Date date1 = dbDate.parse(DateText);
            String dateF = dateFormat.format(date1);
            dateFinal = dateF.split(" ")[0]+"\n"+dateF.split(" ")[1]+" "+dateF.split(" ")[2];
//            Toast.makeText(getContext(), dateFinal, Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        date.setText(dateFinal);
        tr.addView(title);tr.addView(amount);tr.addView(date);
        Expenses.addView(tr);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}