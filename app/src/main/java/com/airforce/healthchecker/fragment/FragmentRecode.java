package com.airforce.healthchecker.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FragmentRecode extends Fragment {

    private LinearLayout recodesLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recode, container, false);
        ArrayList<JSONObject> recodeList = ((MainActivity)getActivity()).getObjectArrayPref("recode");

        recodesLayout = view.findViewById(R.id.recodeLayout);
        for(JSONObject data : recodeList) {
            TextView dateTextView = new TextView(this.getActivity());
            TextView timeTextView = new TextView(this.getActivity());
            try {
                dateTextView.setText(data.getString("date"));
                timeTextView.setText(data.getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recodesLayout.addView(dateTextView);
            recodesLayout.addView(timeTextView);
        }
        return view;
    }

}
