package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

public class FragmentRecode extends Fragment {

    private ImageButton runImageBtn, pushUpImageBtn, sitUpImageBtn;
    private LinearLayout recodeLayout;
    private String type = null;

    @SuppressLint({"ResourceAsColor", "WrongViewCast"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recode, container, false);
        final ArrayList<JSONObject> recodeList = ((MainActivity)getActivity()).getObjectArrayPref("recode");
        recodeLayout = view.findViewById(R.id.recodeLayout);

        runImageBtn = (ImageButton) view.findViewById(R.id.runImageButton);
        pushUpImageBtn = (ImageButton) view.findViewById(R.id.pushUpImageButton);
        sitUpImageBtn = (ImageButton) view.findViewById(R.id.sitUpImageButton);

        runImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<JSONObject> filtered = filteredType(recodeList, "running");
                createRecodesLayouts(filtered);
            }
        });
        pushUpImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<JSONObject> filtered = filteredType(recodeList, "pushUp");
                createRecodesLayouts(filtered);
            }
        });
        sitUpImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<JSONObject> filtered = filteredType(recodeList, "sitUp");
                createRecodesLayouts(filtered);
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

    private ArrayList<JSONObject> filteredType(ArrayList<JSONObject> recodesList ,String type) {
        try {
            ArrayList<JSONObject> filtered = new ArrayList<JSONObject>();
            for(JSONObject json : recodesList) {
                if(json.get("type").equals(type))
                    filtered.add(json);
            }

//            for (int i = 0; i < JA1.length(); i++) {
//                JSONObject json_data = jsonArray.getJSONObject(i);
//                name[i] = json_data.getString("tienda");
//            }

            return filtered;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}
