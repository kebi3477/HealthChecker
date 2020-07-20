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
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class FragmentRecode extends Fragment {

    private TextView dateTextView;
    private ImageButton runImageBtn, pushUpImageBtn, sitUpImageBtn;
    private Button prevButton, nextButton;
    private Map<String, Boolean> btnFlag;
    private LinearLayout recodeLayout;
    private String type = null;
    private ArrayList<JSONObject> recodeList;

    @SuppressLint({"ResourceAsColor", "WrongViewCast"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recode, container, false);
        recodeList = ((MainActivity)getActivity()).getObjectArrayPref("recode");
        recodeLayout = view.findViewById(R.id.recodeLayout);
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
        return view;
    }

    private void createRecodesLayouts(ArrayList<JSONObject> recodeList) {
        if(recodeLayout.getChildCount() > 0) recodeLayout.removeAllViews(); //1개 이상이 있을 경우 초기화
        for(JSONObject data : recodeList) {
            LinearLayout recodesLayout = new LinearLayout(this.getActivity());
            TextView dateTextViews = new TextView(this.getActivity());
            TextView timeTextViews = new TextView(this.getActivity());
            TextView countTextViews = new TextView(this.getActivity());
            try {
                if (String.valueOf(data.get("date")).substring(0, 7).equals(dateTextView.getText().toString())) {
                    timeTextViews.setText("//   " + data.getString("time"));
                    dateTextViews.setText(data.getString("date"));
                    type = data.getString("type");
                    countTextViews.setText(data.getString("count"));
                    //TextView Custom
                    countTextViews.setTextSize(20);
                    countTextViews.setWidth(250);
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
                    recodesLayout.addView(dateTextViews);

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

}
