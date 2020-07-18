package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FragmentRecode extends Fragment {

    private ImageButton runImageBtn, pushUpImageBtn, sitUpImageBtn;
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

        runImageBtn = (ImageButton) view.findViewById(R.id.runImageButton);
        pushUpImageBtn = (ImageButton) view.findViewById(R.id.pushUpImageButton);
        sitUpImageBtn = (ImageButton) view.findViewById(R.id.sitUpImageButton);

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

        createRecodesLayouts(recodeList);
        return view;
    }

    private void createRecodesLayouts(ArrayList<JSONObject> recodeList) {
        if(recodeLayout.getChildCount() > 0) recodeLayout.removeAllViews(); //1개 이상이 있을 경우 초기화

        for(JSONObject data : recodeList) {
            LinearLayout recodesLayout = new LinearLayout(this.getActivity());
            TextView dateTextView = new TextView(this.getActivity());
            TextView timeTextView = new TextView(this.getActivity());
            TextView countTextView = new TextView(this.getActivity());
            try {
                timeTextView.setText("//   " + data.getString("time"));
                dateTextView.setText(data.getString("date"));
                type = data.getString("type");
                countTextView.setText(data.getString("count"));
            } catch (JSONException e) {
                timeTextView.setText("null time");
                dateTextView.setText("null date");
                countTextView.setText("null count");
                e.printStackTrace();
            }
            //TextView Custom
            countTextView.setTextSize(20);
            countTextView.setWidth(250);
            //Layout Custom
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); //Layout Margin 설정
            layoutParams.setMargins(10,10,10,10); //Margin 적용
            recodesLayout.setPaddingRelative(40, 20, 20, 20); //Padding 적용
            if(type.equals("running"))      recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_running);
            else if(type.equals("pushUp"))  recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_pushup);
            else if(type.equals("sitUp"))   recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_situp);

            recodesLayout.addView(countTextView);
            recodesLayout.addView(timeTextView);
            recodesLayout.addView(dateTextView);

            recodeLayout.addView(recodesLayout, layoutParams);
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
}
