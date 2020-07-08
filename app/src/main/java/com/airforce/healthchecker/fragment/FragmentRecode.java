package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentRecode extends Fragment {

    private LinearLayout recodeLayout;
    private String type = null;

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recode, container, false);
        ArrayList<JSONObject> recodeList = ((MainActivity)getActivity()).getObjectArrayPref("recode");
        recodeLayout = view.findViewById(R.id.recodeLayout);

        for(JSONObject data : recodeList) {
            LinearLayout recodesLayout = new LinearLayout(this.getActivity());
            TextView dateTextView = new TextView(this.getActivity());
            TextView timeTextView = new TextView(this.getActivity());
            try {
                timeTextView.setText(data.getString("time"));
                dateTextView.setText(data.getString("date").substring(0,10));
                type = data.getString("type");
            } catch (JSONException e) {
                timeTextView.setText("null time");
                dateTextView.setText("null date");
                e.printStackTrace();
            }


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); //Layout Margin 설정
            layoutParams.setMargins(10,10,10,10); //Margin 적용
            recodesLayout.setPaddingRelative(40, 20, 20, 20); //Padding 적용
            if(type.equals("running")) //Background 설정
                recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_running);
            else if(type.equals("pushUp"))
                recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_pushup);
            else if(type.equals("sitUp"))
                recodesLayout.setBackgroundResource(R.drawable.rounded_recodes_situp);

            recodesLayout.addView(timeTextView);
            recodesLayout.addView(dateTextView);

            recodeLayout.addView(recodesLayout, layoutParams);
        }
        return view;
    }

}
