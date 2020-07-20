package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentGift extends Fragment {

    private TextView giftDistance;
    private ImageView arrow;
    private ArrayList<JSONObject> recodeList;
    private ImageView giftBox1, giftBox2, giftBox3, giftBox4, giftBox5;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift, container, false);
        recodeList = ((MainActivity)getActivity()).getObjectArrayPref("SHARE_PREF","recode");
        float scale = getResources().getDisplayMetrics().density;
        giftDistance = view.findViewById(R.id.giftDistance);
        arrow = view.findViewById(R.id.arrow);
        try {
            Float totalDistance = 0.00f;
            Float arrowMargin = 0.00f;
            for(JSONObject json : recodeList) {
                if(json.get("type").equals("running")) {
                    totalDistance += Float.parseFloat(json.getString("count").substring(0,4));
                }
            }
            totalDistance = totalDistance >= 3f ? 3f : totalDistance;
            if(totalDistance >= 10f) {
                arrowMargin = lerp(370, 2000, 1f/40*totalDistance - 1f/4)*scale;
            } else {
                arrowMargin = lerp(0, 370, totalDistance/10f)*scale;
            }
            arrow.setPadding(Math.round(arrowMargin), 0, 0, 0);
            giftDistance.setText(totalDistance + "km");

            if(totalDistance >= 10f) {
                giftBox1 = view.findViewById(R.id.giftBox1);
                giftBox1.setImageResource(R.mipmap.gift);
            } else if(totalDistance >= 20f) {
                giftBox2 = view.findViewById(R.id.giftBox2);
                giftBox2.setImageResource(R.mipmap.gift);
            } else if(totalDistance >= 20f) {
                giftBox3 = view.findViewById(R.id.giftBox3);
                giftBox3.setImageResource(R.mipmap.gift);
            } else if(totalDistance >= 20f) {
                giftBox4 = view.findViewById(R.id.giftBox4);
                giftBox4.setImageResource(R.mipmap.gift);
            } else {
                giftBox5 = view.findViewById(R.id.giftBox5);
                giftBox5.setImageResource(R.mipmap.gift);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

}
