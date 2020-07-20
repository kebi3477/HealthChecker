package com.airforce.healthchecker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentCompetition extends Fragment {

    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_competition1, container, false);
        ArrayList<JSONObject> recodeList = ((MainActivity) getActivity()).getObjectArrayPref("SHARE_PREF", "recode");
        Float totalDistance = 0.00f;

        textView = view.findViewById(R.id.textView39);

        for(JSONObject json : recodeList) {
            try {
                if(json.get("type").equals("running")) {
                    totalDistance += Float.parseFloat(json.getString("count").substring(0,4));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        textView.setText(totalDistance + "km");

        return view;
    }
}
