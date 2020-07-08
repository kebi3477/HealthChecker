package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FragmentRecode extends Fragment {

    private LinearLayout recodeLayout;

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
            } catch (JSONException e) {
                timeTextView.setText("null time");
                dateTextView.setText("null date");
                e.printStackTrace();
            }

            recodesLayout.setPaddingRelative(20, 20, 20, 20);
            recodesLayout.setBackgroundResource(R.drawable.rounded);

            Drawable backgroundDrawable = recodesLayout.getBackground();
            DrawableCompat.setTint(backgroundDrawable, R.color.colorLightLightGray);

            recodesLayout.addView(timeTextView);
            recodesLayout.addView(dateTextView);

            recodeLayout.addView(recodesLayout);
        }
        return view;
    }

}
