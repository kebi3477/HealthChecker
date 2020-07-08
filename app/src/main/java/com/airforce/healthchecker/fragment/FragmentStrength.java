package com.airforce.healthchecker.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.MainActivity;
import com.airforce.healthchecker.R;

public class FragmentStrength extends Fragment {

    LinearLayout runLayoutBtn, pushUpButton, sitUpButton;
    ImageView runningImage, pushUpImage, sitUpImage;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_strength, container, false);

        runningImage = view.findViewById(R.id.runnningImage);
        pushUpImage = view.findViewById(R.id.pushUpImage);
        sitUpImage = view.findViewById(R.id.sitUpImage);

//        runningImage.setColorFilter(R.color.colorPrimaryDeepBlue); //이미지 색 변경
//        pushUpImage.setColorFilter(R.color.colorPrimaryDeepRed); //이미지 색 변경
//        sitUpImage.setColorFilter(R.color.colorPrimaryDeepGreen); //이미지 색 변경

        runLayoutBtn = view.findViewById(R.id.runLayoutBtn);
        runLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).replaceFragment(new FragmentRunning());
            }
        });

        return view;
    }

}
