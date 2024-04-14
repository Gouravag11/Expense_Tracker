package com.tracker.expensetracker;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tracker.expensetracker.databinding.GraphFragmentBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Graph extends Fragment {

    private GraphFragmentBinding binding;
//    static final String pastWeekQuery = "SELECT * FROM spends WHERE Date >= now() - interval('1 week');";
    DatabaseHelper dbHelper;
    SimpleDateFormat dbDate,dateFor;
    BarChart pastWeekBar;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        dbDate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        dateFor = new SimpleDateFormat("dd-MM-yyyy");

        binding = GraphFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(getContext());
        pastWeekBar = binding.pastWeekBar;
        lastWeekSpend();
    }

    @SuppressLint({"Range","Recycle"})
    public void lastWeekSpend(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] columns = {"Title","Amount","Date"};
        Cursor c = db.query("spends",columns,null,null,null,null,"Date");
        List<List<String>> data = new ArrayList();

        Map<String,Double> vals = new HashMap<String,Double>();

        Calendar cal = Calendar.getInstance();
        Date crrDate = cal.getTime();
        vals.put(dateFor.format(crrDate),0.0);
        Date weekDate = crrDate;
        for (int i = 0; i < 5; i++) {
            cal.add(Calendar.DAY_OF_YEAR,-1);
            weekDate = cal.getTime();
            vals.put(dateFor.format(weekDate),0.0);

        }

        while (c.moveToNext()){
            String amount = c.getString(c.getColumnIndex("Amount"));
            String date = c.getString(c.getColumnIndex("Date"));
            try {
                Date dataDate = dbDate.parse(date);
                if (dataDate.after(weekDate) && dataDate.before(crrDate)){
                    String dateF = dateFor.format(dataDate);
                    if (vals.containsKey(dateF)){
                        Double am = vals.get(dateF) + Double.parseDouble(amount);
                        vals.put(dateF,am);
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        Map<String, Double> sortedMap = new TreeMap<>(vals);
        Set keys = sortedMap.keySet();
        List<Integer> day = new ArrayList<>();
        List<Integer> val = new ArrayList<>();
        for (Iterator j = keys.iterator();j.hasNext();) {
            String key = (String) j.next();
            day.add(Integer.valueOf(key.split("-")[0]));
            Double value = (Double) sortedMap.get(key);
            val.add(value.intValue());
        }

        barEntriesArrayList = new ArrayList<>();
        barEntriesArrayList.add(new BarEntry(day.get(0), val.get(0)));
        barEntriesArrayList.add(new BarEntry(day.get(1), val.get(1)));
        barEntriesArrayList.add(new BarEntry(day.get(2), val.get(2)));
        barEntriesArrayList.add(new BarEntry(day.get(3), val.get(3)));
        barEntriesArrayList.add(new BarEntry(day.get(4), val.get(4)));
        barEntriesArrayList.add(new BarEntry(day.get(5), val.get(5)));

        barDataSet = new BarDataSet(barEntriesArrayList,"Past 6 Days Spending");
        barData = new BarData(barDataSet);
        pastWeekBar.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        pastWeekBar.getDescription().setEnabled(false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}