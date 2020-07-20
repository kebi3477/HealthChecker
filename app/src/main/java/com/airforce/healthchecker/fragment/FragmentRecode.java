package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class FragmentRecode extends Fragment {

    private TextView dateTextView;
    private ImageButton runImageBtn, pushUpImageBtn, sitUpImageBtn;
    private Button prevButton, nextButton;
    private Map<String, Boolean> btnFlag;
    private LinearLayout recodeLayout;
    private LineChart recodeChart;
    private String type = null;
    private ArrayList<JSONObject> recodeList;

    @SuppressLint({"ResourceAsColor", "WrongViewCast"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recode, container, false);
        recodeList = ((MainActivity)getActivity()).getObjectArrayPref("SHARE_PREF","recode");
        recodeLayout = view.findViewById(R.id.recodeLayout);
        recodeChart = view.findViewById(R.id.recordChart);
        btnFlag  = new HashMap<String, Boolean>() {
            {put("running", true); put("pushUp", true); put("sitUp", true);}
        };

        runImageBtn = view.findViewById(R.id.runImageButton);
        pushUpImageBtn = view.findViewById(R.id.pushUpImageButton);
        sitUpImageBtn = view.findViewById(R.id.sitUpImageButton);
        prevButton = view.findViewById(R.id.prevButton);
        nextButton = view.findViewById(R.id.nextButton);
        dateTextView = view.findViewById(R.id.dateTextView);

        // running button
        runImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSortButton("running", runImageBtn, R.color.colorPrimaryBlue, R.color.colorLightGray);
            }
        });

        // pushUp button
        pushUpImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSortButton("pushUp", pushUpImageBtn, R.color.colorPrimaryRed, R.color.colorLightGray);
            }
        });

        // sitUp button
        sitUpImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSortButton("sitUp", sitUpImageBtn, R.color.colorPrimaryGreen, R.color.colorLightGray);
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setYearMonth(-1);
                createRecodesLayouts(recodeList);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setYearMonth(1);
                createRecodesLayouts(recodeList);
            }
        });

        setYearMonth(0);
        createRecodesLayouts(recodeList);
        setChartData(recodeList);
        return view;
    }

    private void createRecodesLayouts(ArrayList<JSONObject> recodeList) {
        if(recodeLayout.getChildCount() > 0) recodeLayout.removeAllViews(); //1개 이상이 있을 경우 초기화

        for(JSONObject data : recodeList) {
            LinearLayout recodesLayout = new LinearLayout(this.getActivity());
            TextView dateTextViews = new TextView(this.getActivity());
            TextView timeTextViews = new TextView(this.getActivity());
            TextView countTextViews = new TextView(this.getActivity());
            TextView rankTextViews = new TextView(this.getActivity());
            try {
                if (String.valueOf(data.get("date")).substring(0, 7).equals(dateTextView.getText().toString())) {
                    timeTextViews.setText("//   " + data.getString("time"));
                    dateTextViews.setText(data.getString("date").split(" ")[0]);
                    type = data.getString("type");
                    countTextViews.setText(data.getString("count"));
                    rankTextViews.setText(data.getString("rank"));
                    //TextView Custom
                    countTextViews.setTextSize(20);
                    countTextViews.setWidth(250);
                    rankTextViews.setPadding(0, 0, 30, 0);
                    timeTextViews.setPadding(0, 0, 30, 0);
                    //Layout Custom
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); //Layout Margin 설정
                    layoutParams.setMargins(10, 10, 10, 10); //Margin 적용
                    recodesLayout.setPaddingRelative(40, 20, 20, 20); //Padding 적용
                    if (type.equals("running"))
                        recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_running);
                    else if (type.equals("pushUp"))
                        recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_pushup);
                    else if (type.equals("sitUp"))
                        recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_situp);

                    recodesLayout.addView(countTextViews);
                    recodesLayout.addView(timeTextViews);
                    recodesLayout.addView(rankTextViews);
                    recodesLayout.addView(dateTextViews);

                    recodeLayout.setPadding(20, 0, 20, 0);
                    recodeLayout.addView(recodesLayout, layoutParams);
                }
            } catch (Exception e) {
                timeTextViews.setText("null time");
                dateTextViews.setText("null date");
                countTextViews.setText("null count");
                e.printStackTrace();
            }
        }
    }

    private ArrayList<JSONObject> filteredType(ArrayList<JSONObject> recodesList) {
        try {
            ArrayList<JSONObject> filtered = new ArrayList<JSONObject>();
            for(JSONObject json : recodesList) {
                if(btnFlag.get("running") && json.get("type").equals("running")) filtered.add(json);
                if(btnFlag.get("pushUp") && json.get("type").equals("pushUp")) filtered.add(json);
                if(btnFlag.get("sitUp") && json.get("type").equals("sitUp")) filtered.add(json);
            }
            return filtered;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onClickSortButton(String type, ImageButton button, int activeColor, int inactiveColor) {
        Boolean targetActive = !btnFlag.get(type);
        btnFlag.put(type, targetActive);

        if(targetActive)
            button.setBackgroundColor(getResources().getColor(activeColor));
        else
            button.setBackgroundColor(getResources().getColor(inactiveColor));

        ArrayList<JSONObject> filtered = filteredType(recodeList);
        createRecodesLayouts(filtered);
    }

    private void setYearMonth(int plus) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM");
        Calendar cal = Calendar.getInstance();
        int month, year;
        if(plus != 0) {
            month = Integer.parseInt(String.valueOf(dateTextView.getText()).substring(5,7));
            year = Integer.parseInt(String.valueOf(dateTextView.getText()).substring(0,4));
            month = month + plus;
            cal.set(Calendar.MONTH, month-1);
            cal.set(Calendar.YEAR, year);
        }
        dateTextView.setText(simpleDateFormat.format(cal.getTime()));
    }

    private void setChartData(ArrayList<JSONObject> datas){
        ArrayList<JSONObject> runningArray = new ArrayList<JSONObject>();
        ArrayList<JSONObject> pushUpArray = new ArrayList<JSONObject>();
        ArrayList<JSONObject> sitUpArray = new ArrayList<JSONObject>();
        try {
            for(JSONObject data : datas){
                if(data.get("type").equals("running")){
                    runningArray.add(data);
                }else if(data.get("type").equals("pushUp")){
                    pushUpArray.add(data);
                }else if(data.get("type").equals("sitUp")){
                    sitUpArray.add(data);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Comparator<JSONObject> myComparator = new Comparator<JSONObject>() {
            private static final String KEY_NAME = "date";
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();
                try {
                    valA = (String) a.get(KEY_NAME);
                    valB = (String) b.get(KEY_NAME);
                }
                catch (JSONException e) {
                }
                return valA.compareTo(valB);
            }
        };
        runningArray.sort(myComparator);
        pushUpArray.sort(myComparator);
        sitUpArray.sort(myComparator);



        ArrayList<Entry> runningEntry = new ArrayList<>();
        ArrayList<Entry> pushUpEntry = new ArrayList<>();
        ArrayList<Entry> sitUpEntry = new ArrayList<>();
        int i=0;
        try {
            for(JSONObject data:runningArray){
                String value = data.get("count").toString();
                float num = Float.parseFloat(value.substring(0,value.length()-2));
                runningEntry.add(new Entry(num, i));
                i++;
            }
            i=0;
            for(JSONObject data:pushUpArray){
                String value = data.get("count").toString();
                float num = Float.parseFloat(value.substring(0,value.length()-1));
                pushUpEntry.add(new Entry(num, i));
                i++;
            }
            i=0;
            for(JSONObject data:sitUpArray){
                String value = data.get("count").toString();
                float num = Float.parseFloat(value.substring(0,value.length()-1));
                sitUpEntry.add(new Entry(num, i));
                i++;
            }
        }
        catch (JSONException e) {
        }

        ArrayList<LineDataSet> lines = new ArrayList<LineDataSet> ();
        String[] xAxis = new String[] {"1", "2", "3", "4", "5","6"};
        LineDataSet runningDataSet = new LineDataSet(runningEntry, "달리기");
        runningDataSet.setDrawFilled(true);
        runningDataSet.setDrawFilled(false);
        lines.add(runningDataSet);

        LineDataSet pushUpDataSet = new LineDataSet(pushUpEntry, "팔굽혀펴기");
        pushUpDataSet.setDrawFilled(true);
        pushUpDataSet.setColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryRed));
        pushUpDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
        pushUpDataSet.setDrawFilled(false);
        pushUpDataSet.setAxisDependency(recodeChart.getAxisRight().getAxisDependency());
        lines.add(pushUpDataSet);

        LineDataSet sitUpDataSet = new LineDataSet(sitUpEntry, "윗몸일으키기");
        sitUpDataSet.setDrawFilled(true);
        sitUpDataSet.setColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryRed));
        sitUpDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryRed));
        sitUpDataSet.setDrawFilled(false);
        sitUpDataSet.setAxisDependency(recodeChart.getAxisRight().getAxisDependency());
        lines.add(sitUpDataSet);

        XAxis xAxisSet = recodeChart.getXAxis(); // x 축 설정
        xAxisSet.setDrawGridLines(false);
        xAxisSet.setDrawLabels(false);

        YAxis yAxisLeftSet = recodeChart.getAxisLeft(); // y 축 설정
        yAxisLeftSet.setDrawGridLines(false);
        yAxisLeftSet.setDrawAxisLine(false);
        yAxisLeftSet.setDrawLabels(false);

        YAxis yAxisRightSet = recodeChart.getAxisRight(); // y 축 설정
        yAxisRightSet.setDrawGridLines(false);
        yAxisRightSet.setDrawAxisLine(false);
        yAxisRightSet.setDrawLabels(false);
        yAxisRightSet.setSpaceTop(10f);



        recodeChart.setData(new LineData(xAxis,lines));
        recodeChart.setBackgroundColor(getResources().getColor(R.color.colorLightGray));
        recodeChart.setGridBackgroundColor(getResources().getColor(R.color.colorLightGray));
        recodeChart.animateY(500);
    }
}
