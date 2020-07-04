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

import java.util.ArrayList;
import java.util.Map;

public class FragmentRecode extends Fragment {

    private Button test;
    private LinearLayout recodesLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recode, container, false);
        ArrayList<String> recodeList = ((MainActivity)getActivity()).getStringArrayPref("endTime");

        recodesLayout = view.findViewById(R.id.recodeLayout);
        for(String data : recodeList) {
            TextView test2 = new TextView(this.getActivity());
            test2.setText(data);
            recodesLayout.addView(test2);
        }
        return view;
    }

}
